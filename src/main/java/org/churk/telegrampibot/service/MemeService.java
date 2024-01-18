package org.churk.telegrampibot.service;

import lombok.extern.slf4j.Slf4j;
import org.churk.telegrampibot.client.MemeClient;
import org.churk.telegrampibot.config.MemeConfig;
import org.churk.telegrampibot.utility.FileDownloader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class MemeService {
    private final MemeConfig memeConfig;
    private final MemeClient memeClient;

    public MemeService(MemeConfig memeConfig, MemeClient memeClient) {
        this.memeConfig = memeConfig;
        this.memeClient = memeClient;
    }

    public Optional<File> getMemeFromSubreddit(String subreddit) {
        Map<String, Object> map = memeClient.getMemeFromSubreddit(subreddit);
        return getFile(map);
    }

    public Optional<File> getMeme() {
        Map<String, Object> map = memeClient.getMeme();
        return getFile(map);
    }

    private Optional<File> getFile(Map<String, Object> map) {
        String apiUrl = (String) map.get("url");
        FileDownloader.downloadFileFromUrl(apiUrl, memeConfig.getDownloadPath(), memeConfig.getFileName());
        String downloadedFilePath = FileDownloader.waitForDownload(memeConfig.getDownloadPath(), memeConfig.getFileName());
        if (downloadedFilePath == null) {
            return Optional.empty();
        }
        return Optional.of(new File(downloadedFilePath));
    }
}
