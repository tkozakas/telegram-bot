package org.churk.telegrambot.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "redditClient", url = "https://meme-api.com")
public interface RedditClient {

    @GetMapping("/gimme")
    Map<String, Object> getRedditMeme();

    @GetMapping("/gimme/{subreddit}")
    Map<String, Object> getRedditMemeFromSubreddit(@PathVariable String subreddit);
}
