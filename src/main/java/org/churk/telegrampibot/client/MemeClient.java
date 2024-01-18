package org.churk.telegrampibot.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "memeClient", url = "https://meme-api.herokuapp.com")
public interface MemeClient {

    @GetMapping("/gimme")
    String getMeme();

    @GetMapping("/gimme/${subreddit}")
    String getMemeFromSubreddit(String subreddit);

    @GetMapping("/gimme/${subreddit}/${count}")
    String getMemeFromSubreddit(String subreddit, int count);
}
