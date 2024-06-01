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
    private static final String COMPRESSION_QUALITY = "10";
    private static final int BUFFER_SIZE = 5048 * 1024;
    private static final ExecutorService executorService = Executors.newFixedThreadPool(10);

    public static String generateUniqueFileName(String extension) {
        return UUID.randomUUID() + "." + extension;
    }

    public static Optional<File> downloadAndCompressMedia(String apiUrl, DownloadMediaProperties properties, String extension) {
        try {
            String fileName = generateUniqueFileName(extension);

            Future<File> downloadTask = executorService.submit(() -> downloadFileFromUrl(apiUrl, properties.getPath(), fileName));
            File downloadedFile = downloadTask.get();

            Future<File> compressTask = executorService.submit(() -> {
                String compressedFilePath = Paths.get(properties.getPath(), FilenameUtils.getBaseName(fileName) + "_compressed" + extension).toString();
                compressFile(extension, downloadedFile.getPath(), compressedFilePath);
                return new File(compressedFilePath);
            });
            File compressedFile = compressTask.get();
            
            Files.deleteIfExists(downloadedFile.toPath());
            return Optional.of(compressedFile);
        } catch (Exception e) {
            log.error("Error in download/compression", e);
            return Optional.empty();
        }
    }

    public static File downloadFileFromUrl(String apiUrl, String downloadDirectory, String fileName) throws IOException {
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

    private static void compressFile(String extension, String filePath, String compressedFilePath) throws IOException {
        FFmpeg builder = FFmpeg.atPath()
                .addInput(UrlInput.fromPath(Paths.get(filePath)))
                .addOutput(UrlOutput.toPath(Paths.get(compressedFilePath)))
                .addArguments("-loglevel", "panic");
        switch (extension) {
            case ".gif" -> builder
                    .setComplexFilter(FilterGraph.of(
                            FilterChain.of(
                                    Filter.withName("fps").addArgument("fps=8"),
                                    Filter.withName("setpts").addArgument("4/10*PTS")
                            )
                    ));
            case ".mp4" -> builder.addArguments("-q:v", COMPRESSION_QUALITY)
                    .addArgument("-vcodec")
                    .addArgument("libx265")
                    .addArguments("-crf", "30")
                    .addArguments("-preset", "fast")
                    .setComplexFilter(FilterGraph.of(
                            FilterChain.of(
                                    Filter.withName("fps").addArgument("fps=12")
                            )
                    ));
            default -> builder.addArguments("-c:v", "mjpeg")
                    .addArguments("-q:v", COMPRESSION_QUALITY);
        }
        builder.execute();
        log.info("File compressed and saved as: {}", compressedFilePath);
    }

    public static File convertGifToMp4(File file) {
        String filePath = file.getPath();
        String mp4FilePath = filePath.replace(".gif", ".mp4");
        FFmpeg.atPath()
                .addInput(UrlInput.fromPath(Paths.get(filePath)))
                .addOutput(UrlOutput.toPath(Paths.get(mp4FilePath)))
                .addArguments("-loglevel", "panic")
                .addArguments("-c:v", "libx265")
                .addArguments("-crf", "30")
                .addArguments("-preset", "fast")
                .execute();
        return new File(mp4FilePath);
    }
}
