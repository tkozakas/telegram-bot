package org.churk.telegrampibot.utility;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;

@Slf4j
@Component
public class FileDownloader {
    private static final int timeOutSeconds = 30;

    public static String waitForDownload(String downloadDirectory, String fileName) {
        long startTime = System.currentTimeMillis();
        while (true) {
            if (System.currentTimeMillis() - startTime > timeOutSeconds * 1000) {
                log.error("Timeout while waiting for meme download");
                return null;
            }
            if (isDownloaded(downloadDirectory, fileName)) {
                return downloadDirectory + fileName;
            }
        }
    }

    private static boolean isDownloaded(String downloadDirectory, String fileName) {
        String filePathString = downloadDirectory + fileName;
        File f = new File(filePathString);
        return f.exists() && !f.isDirectory();
    }

    public static void downloadFileFromUrl(String apiUrl, String downloadDirectory, String fileName) {
        String filePath = downloadDirectory + fileName;
        log.info("Downloading file from {}", apiUrl);
        try (InputStream in = new BufferedInputStream(new URL(apiUrl).openStream())) {
            try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
                byte[] dataBuffer = new byte[1024];
                int bytesRead;

                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                }
            }
        } catch (IOException e) {
            log.error("Error while downloading meme", e);
        }
        log.info("Meme downloaded successfully");
    }
}
