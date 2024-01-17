package org.churk.telegrampibot.utility;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
@Component
public class MemeDownloader {
    private static final String apiUrl = "https://meme-api.com/gimme";
    private static final String downloadDirectory = "src/main/resources/temp/";
    private static final String memeFileName = "meme";
    private static final String fileExtension = ".png";
    private static final int timeoutSeconds = 30;

    public static String waitForDownload() {
        long startTime = System.currentTimeMillis();
        while (true) {
            if (System.currentTimeMillis() - startTime > timeoutSeconds * 1000) {
                log.error("Timeout while waiting for meme download");
                return null;
            }
            if (isDownloaded()) {
                return downloadDirectory + memeFileName + fileExtension;
            }
        }
    }

    private static boolean isDownloaded() {
        String filePathString = downloadDirectory + memeFileName + fileExtension;
        File f = new File(filePathString);
        return f.exists() && !f.isDirectory();
    }

    public static void downloadMeme(String subreddit) {
        try {
            String apiUrl = MemeDownloader.apiUrl;
            if (subreddit != null) {
                apiUrl = apiUrl + "/" + subreddit;
            }
            String imageUrl = getImageUrl(apiUrl);
            log.info("Downloading meme from {}", apiUrl);
            String filePath = downloadDirectory + memeFileName + fileExtension;
            try (InputStream in = new BufferedInputStream(new URL(imageUrl).openStream())) {
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
        } catch (IOException e) {
            log.error("Error while downloading meme", e);
        }
    }

    public static String getImageUrl(String apiUrl) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("GET");

        InputStream inputStream = httpURLConnection.getInputStream();
        byte[] responseBytes = inputStream.readAllBytes();
        String response = new String(responseBytes);

        JSONObject jsonObject = new JSONObject(response);
        return jsonObject.getString("url");
    }
}
