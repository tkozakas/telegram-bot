package org.churk.telegrambot.utility;

import com.github.kokorin.jaffree.ffmpeg.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.churk.telegrambot.config.DownloadMediaProperties;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
@Component
public class FileDownloader {
    private static final String FPS = "12";
    private static final String COMPRESSION_QUALITY = "10";
    private static final int BUFFER_SIZE = 5048 * 1024;
    private static final ExecutorService executorService = Executors.newFixedThreadPool(10);

    public static String generateUniqueFileName(String extension) {
        return UUID.randomUUID() + extension;
    }

    public static Optional<File> downloadAndCompressMedia(String apiUrl, DownloadMediaProperties properties, String extension) {
        try {
            String fileName = generateUniqueFileName(extension);
            File downloadedFile = downloadFile(apiUrl, properties.getPath(), fileName);
            File compressedFile = compressMedia(downloadedFile, properties.getPath(), extension, FPS, COMPRESSION_QUALITY);
            deleteFile(downloadedFile);
            return Optional.of(compressedFile);
        } catch (Exception e) {
            log.error("Error while downloading and compressing media", e);
            return Optional.empty();
        }
    }

    private static File downloadFile(String apiUrl, String downloadDirectory, String fileName) throws Exception {
        Future<File> downloadTask = executorService.submit(() -> downloadFileFromUrl(apiUrl, downloadDirectory, fileName));
        return downloadTask.get();
    }

    private static File compressMedia(File file, String directory, String extension, String fps, String quality) throws Exception {
        Future<File> compressTask = executorService.submit(() -> {
            String compressedFilePath = Paths.get(directory, FilenameUtils.getBaseName(file.getName()) + "_compressed" + extension).toString();
            compressFile(extension, file.getPath(), compressedFilePath, fps, quality);
            return new File(compressedFilePath);
        });
        return compressTask.get();
    }

    private static File downloadFileFromUrl(String apiUrl, String downloadDirectory, String fileName) throws IOException {
        String filePath = Paths.get(downloadDirectory, fileName).toString();
        log.info("Downloading file from {}", apiUrl);

        try (InputStream in = new BufferedInputStream(new URL(apiUrl).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
            byte[] dataBuffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, BUFFER_SIZE)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
            log.info("Downloaded file saved to: {}", filePath);
            return new File(filePath);
        } catch (IOException e) {
            log.error("Error while downloading file", e);
            throw e;
        }
    }

    private static void compressFile(String extension, String filePath, String compressedFilePath, String fps, String compressionQuality) {
        FFmpeg builder = FFmpeg.atPath()
                .addInput(UrlInput.fromPath(Paths.get(filePath)))
                .addOutput(UrlOutput.toPath(Paths.get(compressedFilePath)))
                .addArguments("-loglevel", "info");

        configureFFmpegBuilder(extension, builder, fps, compressionQuality);

        builder.execute();
        log.info("File compressed and saved as: {}", compressedFilePath);
    }

    private static void configureFFmpegBuilder(String extension, FFmpeg builder, String fps, String compressionQuality) {
        switch (extension) {
            case ".gif" -> builder
                    .setComplexFilter(FilterGraph.of(
                            FilterChain.of(
                                    Filter.withName("fps").addArgument("fps=8"),
                                    Filter.withName("setpts").addArgument("4/10*PTS")
                            )
                    ));
            case ".mp4" -> builder.addArguments("-q:v", compressionQuality)
                    .addArgument("-vcodec")
                    .addArgument("libx265")
                    .addArguments("-crf", compressionQuality)
                    .addArguments("-preset", "fast")
                    .setComplexFilter(FilterGraph.of(
                            FilterChain.of(
                                    Filter.withName("fps").addArgument("fps=" + fps)
                            )
                    ));
            default -> builder.addArguments("-c:v", "mjpeg")
                    .addArguments("-q:v", compressionQuality);
        }
    }

    private static void convertGif(String sourceFilePath, String targetFilePath) {
        FFmpeg.atPath()
                .addInput(UrlInput.fromPath(Paths.get(sourceFilePath)))
                .addOutput(UrlOutput.toPath(Paths.get(targetFilePath)))
                .addArguments("-loglevel", "info")
                .addArguments("-movflags", "faststart")
                .addArguments("-pix_fmt", "yuv420p")
                .execute();
        log.info("GIF converted to MP4 and saved as: {}", targetFilePath);
    }

    public static Optional<File> convertGifToMp4(File file, DownloadMediaProperties properties) {
        try {
            String extension = ".mp4";
            String mp4FilePath = Paths.get(properties.getPath(), FilenameUtils.getBaseName(file.getName()) + extension).toString();
            convertGif(file.getPath(), mp4FilePath);
            deleteFile(file);
            return Optional.of(new File(mp4FilePath));
        } catch (Exception e) {
            log.error("Error while converting gif to mp4", e);
            return Optional.empty();
        }
    }

    public static void deleteFile(File file) {
        if (file != null && file.exists()) {
            try {
                Files.deleteIfExists(file.toPath());
                log.info("File deleted successfully: {}", file.getAbsolutePath());
            } catch (IOException e) {
                log.error("Error while deleting file: {}", file.getAbsolutePath(), e);
            }
        }
    }
}
