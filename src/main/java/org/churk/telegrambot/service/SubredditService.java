package org.churk.telegrambot.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrambot.client.RedditClient;
import org.churk.telegrambot.config.DownloadMediaProperties;
import org.churk.telegrambot.model.RedditPost;
import org.churk.telegrambot.model.Subreddit;
import org.churk.telegrambot.repository.SubredditRepository;
import org.churk.telegrambot.utility.FileDownloader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubredditService {
    private final SubredditRepository subredditRepository;
    private final DownloadMediaProperties redditProperties;
    private final RedditClient redditClient;

    public void addSubreddit(Long chatId, String subreddit) {
        subredditRepository.save(new Subreddit(chatId, subreddit));
    }

    public void deleteSubreddit(Long chatId, String subreddit) {
        subredditRepository.findByChatIdAndSubredditName(chatId, subreddit)
                .ifPresent(subredditRepository::delete);
    }

    public boolean isValidSubreddit(String subreddit) {
        return !getRedditPosts(subreddit).isEmpty();
    }

    public boolean existsByChatIdAndSubredditName(Long chatId, String subreddit) {
        return subredditRepository.existsByChatIdAndSubredditName(chatId, subreddit);
    }

    public List<Subreddit> getSubreddits(Long chatId) {
        return subredditRepository.findAllByChatId(chatId);
    }

    public Optional<RedditPost> getMemeFromSubreddit(String subreddit) throws feign.FeignException.NotFound {
        List<RedditPost> redditMeme = getRedditPosts(subreddit);
        if (redditMeme.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(redditMeme.get(ThreadLocalRandom.current().nextInt(redditMeme.size())));
    }

    private List<RedditPost> getRedditPosts(String subreddit) {
        try {
            Map<String, Object> jsonResponse = redditClient.getRedditMemeFromSubreddit(subreddit);
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> responseMap = mapper.convertValue(jsonResponse, new TypeReference<>() {
            });
            if (responseMap.containsKey("memes") && responseMap.get("memes") instanceof List) {
                List<Map<String, Object>> memes = (List<Map<String, Object>>) responseMap.get("memes");
                return memes.stream()
                        .map(memeMap -> mapper.convertValue(memeMap, RedditPost.class))
                        .collect(Collectors.toList());
            }
            return List.of(mapper.convertValue(responseMap, RedditPost.class));

        } catch (FeignException.NotFound e) {
            log.error("Subreddit not found", e);
        } catch (FeignException e) {
            log.error("Error with Feign client", e);
        }
        return List.of();
    }

    public Optional<File> getFile(RedditPost redditPost) {
        String mediaUrl = redditPost.getUrl();
        String extension = mediaUrl.substring(mediaUrl.lastIndexOf("."));
        return FileDownloader.downloadAndCompressMediaAsync(mediaUrl, redditProperties, extension);
    }
}
