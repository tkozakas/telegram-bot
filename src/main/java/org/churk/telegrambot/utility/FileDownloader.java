package org.churk.telegrambot.utility;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;

@Slf4j
@Component
public class FileDownloader {
    private static final int timeOutSeconds = 30;

    public static String waitForDownload(String downloadDirectory, String fileName, String extension) {
        long startTime = System.currentTimeMillis();
        while (true) {
            if (System.currentTimeMillis() - startTime > timeOutSeconds * 1000) {
                log.error("Timeout while waiting for meme download");
                return null;
            }
            if (isDownloaded(downloadDirectory, fileName, extension)) {
                return downloadDirectory + fileName + extension;
            }
        }
    }

    private static boolean isDownloaded(String downloadDirectory, String fileName, String extension) {
        String filePathString = downloadDirectory + fileName + extension;
        File f = new File(filePathString);
        return f.exists() && !f.isDirectory();
    }

    public static void downloadFileFromUrl(String apiUrl, String downloadDirectory, String fileName, String extension) {
        String filePath = downloadDirectory + fileName + extension;
        log.info("Downloading file from {}", apiUrl);

        // Increase the buffer size for potentially faster downloads
        // Adjust this size based on your specific requirements and resource constraints
        int bufferSize = 512 * 1024; // 1014 KB

        try (InputStream in = new BufferedInputStream(new URL(apiUrl).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
            byte[] dataBuffer = new byte[bufferSize];
            int bytesRead;

            while ((bytesRead = in.read(dataBuffer, 0, bufferSize)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
            log.info("File downloaded successfully: {}", filePath);
        } catch (IOException e) {
            log.error("Error while downloading file", e);
        }
    }


}
