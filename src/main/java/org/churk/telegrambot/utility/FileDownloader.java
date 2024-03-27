package org.churk.telegrambot.utility;

import com.github.kokorin.jaffree.ffmpeg.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Component
public class FileDownloader {
    private static final int TIME_OUT_SECONDS = 30;
    private static final String COMPRESSION_QUALITY = "10";


    public static String waitForDownload(String downloadDirectory, String fileName, String extension) {
        String filePath = downloadDirectory + fileName + "_compressed" + extension;
        long startTime = System.currentTimeMillis();
        while (true) {
            if (System.currentTimeMillis() - startTime > TIME_OUT_SECONDS * 1000) {
                log.error("Timeout while waiting for meme download");
                return null;
            }
            if (isDownloaded(filePath)) {
                log.info("File downloaded successfully: {}", filePath);
                long endTime = System.currentTimeMillis();
                long elapsedTimeInSeconds = (endTime - startTime) / 1000;
                log.info("File download time: " + elapsedTimeInSeconds + " seconds");
                return filePath;
            }
        }
    }

    private static boolean isDownloaded(String filePath) {
        File f = new File(filePath);
        return f.exists() && !f.isDirectory();
    }

    public static void downloadFileFromUrl(String apiUrl, String downloadDirectory, String fileName, String extension) {
        String filePath = downloadDirectory + fileName + extension;
        String compressedFilePath = downloadDirectory + fileName + "_compressed" + extension;
        log.info("Downloading file from {}", apiUrl);

        int bufferSize = 1024 * 1024;

        try (InputStream in = new BufferedInputStream(new URL(apiUrl).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
            byte[] dataBuffer = new byte[bufferSize];
            int bytesRead;

            while ((bytesRead = in.read(dataBuffer, 0, bufferSize)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }

            compressFile(extension, filePath, compressedFilePath);
            File file = new File(filePath);
            file.deleteOnExit();
        } catch (IOException e) {
            log.error("Error while downloading or compressing file", e);
        }
    }

    private static void compressFile(String extension, String filePath, String compressedFilePath) {
        if (extension.equals(".gif")) {
            compressGif(filePath, compressedFilePath);
        } else {
            compressImage(filePath, compressedFilePath);
        }
    }

    private static void compressImage(String filePath, String compressedFilePath) {
        try {
            deleteIfExists(compressedFilePath);
            FFmpeg.atPath()
                    .addInput(UrlInput.fromPath(Paths.get(filePath)))
                    .addOutput(UrlOutput.toPath(Paths.get(compressedFilePath))
                            .addArguments("-c:v", "mjpeg")
                            .addArguments("-q:v", COMPRESSION_QUALITY))
                    .execute();
            log.info("File compressed and saved as: {}", compressedFilePath);
        } catch (IOException e) {
            log.error("Error compressing the image: {}", e.getMessage());
        }
    }

    private static void compressGif(String filePath, String compressedFilePath) {
        try {
            deleteIfExists(compressedFilePath);
            FFmpeg.atPath()
                    .addInput(UrlInput.fromPath(Paths.get(filePath)))
                    .addOutput(UrlOutput.toPath(Paths.get(compressedFilePath)))
                    .setComplexFilter(FilterGraph.of(
                            FilterChain.of(
                                    Filter.withName("fps").addArgument("fps=8"),
                                    Filter.withName("setpts").addArgument("4/10*PTS")
                            )
                    ))
                    .execute();
            log.info("File compressed and saved as: {}", compressedFilePath);
        } catch (IOException e) {
            log.error("Error compressing the gif: {}", e.getMessage());
        }
    }

    private static void deleteIfExists(String filePath) throws IOException {
        Path compressedPath = Paths.get(filePath);
        if (Files.exists(compressedPath)) {
            Files.delete(compressedPath);
        }
    }
}
