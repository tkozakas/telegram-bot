package org.churk.telegrambot.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "memeClient", url = "https://meme-api.com")
public interface MemeClient {

    @GetMapping("/gimme")
    Map<String, Object> getMeme();

    @GetMapping("/gimme/{subreddit}")
    Map<String, Object> getMemeFromSubreddit(@PathVariable String subreddit);
}
