package org.churk.telegrampibot.service;

import lombok.extern.slf4j.Slf4j;
import org.churk.telegrampibot.client.MemeClient;
import org.churk.telegrampibot.config.MemeConfig;
import org.churk.telegrampibot.utility.FileDownloader;
import org.jvnet.hk2.annotations.Service;

import java.io.File;
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
        String apiUrl = memeClient.getMemeFromSubreddit(subreddit);
        FileDownloader.downloadFileFromUrl(apiUrl);
        String downloadedFilePath = FileDownloader.waitForDownload(memeConfig.getDownloadPath(), memeConfig.getFileName());
        if (downloadedFilePath == null) {
            return Optional.empty();
        }
        return Optional.of(new File(downloadedFilePath));
    }

    public Optional<File> getMeme() {
        String apiUrl = memeClient.getMeme();
        FileDownloader.downloadFileFromUrl(apiUrl);
        String downloadedFilePath = FileDownloader.waitForDownload(memeConfig.getDownloadPath(), memeConfig.getFileName());
        if (downloadedFilePath == null) {
            return Optional.empty();
        }
        return Optional.of(new File(downloadedFilePath));
    }
}
