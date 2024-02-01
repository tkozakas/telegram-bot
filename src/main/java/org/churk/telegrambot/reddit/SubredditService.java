package org.churk.telegrambot.reddit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrambot.config.DownloadMediaProperties;
import org.churk.telegrambot.utility.FileDownloader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

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
        return getRedditPost(subreddit).isPresent();
    }

    public boolean existsByChatIdAndSubredditName(Long chatId, String subreddit) {
        return subredditRepository.existsByChatIdAndSubredditName(chatId, subreddit);
    }

    public List<Subreddit> getSubreddits(Long chatId) {
        return subredditRepository.findAllByChatId(chatId);
    }

    public Optional<File> getMemeFromSubreddit(String subreddit) throws feign.FeignException.NotFound {
        Optional<RedditPost> redditMeme = getRedditPost(subreddit);
        return redditMeme.isPresent() ?
                getFile(redditMeme.get()).join() :
                Optional.empty();
    }

    private Optional<RedditPost> getRedditPost(String subreddit) {
        try {
            String jsonResponse = redditClient.getRedditMemeFromSubreddit(subreddit);
            ObjectMapper mapper = new ObjectMapper();
            return Optional.ofNullable(mapper.readValue(jsonResponse, RedditPost.class));
        } catch (FeignException e) {
            log.error("Error with Feign client", e);
        } catch (JsonProcessingException e) {
            log.error("Error with parsing JSON", e);
        }
        return Optional.empty();
    }

    private CompletableFuture<Optional<File>> getFile(RedditPost redditPost) {
        String mediaUrl = redditPost.getUrl();
        String extension = mediaUrl.substring(mediaUrl.lastIndexOf("."));
        return FileDownloader.downloadAndCompressMediaAsync(mediaUrl, redditProperties, extension);
    }
}
