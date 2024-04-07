package org.churk.telegrambot.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "redditClient", url = "https://meme-api.com")
public interface RedditClient {

    @GetMapping("/gimme")
    String getRedditMeme();

    @GetMapping("/gimme/{subreddit}")
    String getRedditMemeFromSubreddit(@PathVariable String subreddit);
}
