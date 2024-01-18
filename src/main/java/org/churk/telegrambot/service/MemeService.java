package org.churk.telegrambot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrambot.client.MemeClient;
import org.churk.telegrambot.config.MemeProperties;
import org.churk.telegrambot.utility.FileDownloader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemeService {
    private final MemeProperties memeProperties;
    private final MemeClient memeClient;
    public Optional<File> getMemeFromSubreddit(String subreddit) {
        try {
            Map<String, Object> map = memeClient.getMemeFromSubreddit(subreddit);
            return getFile(map);
        } catch (feign.FeignException.NotFound e) {
            log.error("Subreddit not found: {}", e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error fetching meme from subreddit: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<File> getMeme() {
        Map<String, Object> map = memeClient.getMeme();
        return getFile(map);
    }

    private Optional<File> getFile(Map<String, Object> map) {
        String apiUrl = (String) map.get("url");
        String extension = apiUrl.substring(apiUrl.lastIndexOf("."));

        FileDownloader.downloadFileFromUrl(apiUrl, memeProperties.getDownloadPath(), memeProperties.getFileName(), extension);
        String downloadedFilePath = FileDownloader.waitForDownload(memeProperties.getDownloadPath(), memeProperties.getFileName(), extension);
        if (downloadedFilePath == null) {
            return Optional.empty();
        }
        return Optional.of(new File(downloadedFilePath));
    }
}
