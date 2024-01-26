package org.churk.telegrambot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrambot.client.RedditClient;
import org.churk.telegrambot.config.DownloadMediaProperties;
import org.churk.telegrambot.model.reddit.RedditPost;
import org.churk.telegrambot.utility.FileDownloader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedditService {
    private final DownloadMediaProperties redditProperties;
    private final RedditClient redditClient;

    public Optional<File> getMemeFromSubreddit(String subreddit) throws feign.FeignException.NotFound {
        try {
            String jsonResponse = redditClient.getRedditMemeFromSubreddit(subreddit);
            ObjectMapper mapper = new ObjectMapper();
            RedditPost redditMeme = mapper.readValue(jsonResponse, RedditPost.class);
            return getFile(redditMeme);
        } catch (JsonProcessingException e) {
            log.error("Error while parsing reddit response", e);
            return Optional.empty();
        }
    }

    private Optional<File> getFile(RedditPost reddit) {
        String apiUrl = reddit.getUrl();
        String extension = apiUrl.substring(apiUrl.lastIndexOf("."));
        return FileDownloader.downloadAndCompressMedia(apiUrl, redditProperties, extension);
    }
}
