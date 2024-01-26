package org.churk.telegrambot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrambot.client.RedditClient;
import org.churk.telegrambot.config.DownloadMediaProperties;
import org.churk.telegrambot.utility.FileDownloader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedditService {
    private final DownloadMediaProperties redditProperties;
    private final RedditClient redditClient;

    public Optional<File> getMemeFromSubreddit(String subreddit) throws feign.FeignException.NotFound {
        Map<String, Object> map = redditClient.getRedditMemeFromSubreddit(subreddit);
        return getFile(map);
    }

    private Optional<File> getFile(Map<String, Object> map) {
        String apiUrl = (String) map.get("url");
        String extension = apiUrl.substring(apiUrl.lastIndexOf("."));
        return FileDownloader.downloadAndCompressMedia(apiUrl, redditProperties, extension);
    }
}
