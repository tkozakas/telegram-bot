package org.churk.telegrambot.client;

import org.churk.telegrambot.model.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "meme-client", url = "${meme-api.url}")
public interface MemeClient {
    @PostMapping(value = "/gpt/prompt")
    ResponseEntity<String> getGpt(@RequestBody GptRequest gptRequest);

    @PostMapping("/meme/reddit/{subreddit}/{count}")
    ResponseEntity<List<RedditPost>> getRedditPost(@PathVariable("subreddit") String subreddit, @PathVariable("count") int count);

    @PostMapping("/meme/shitpost/{search}")
    ResponseEntity<Shitpost> getShitPost(@PathVariable("search") String search);

    @PostMapping("/meme/shitpost")
    ResponseEntity<Shitpost> getShitPost();

    @PostMapping("/meme/shitpost/quote")
    ResponseEntity<Quote> getShitPostQuote();

    @PostMapping("/news/{category}")
    ResponseEntity<List<Article>> getNews(@PathVariable("category") String category);

    @PostMapping(value = "/elevenlabs/tts", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    ResponseEntity<byte[]> getTts(@RequestBody String text);
}
