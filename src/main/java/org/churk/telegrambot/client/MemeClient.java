package org.churk.telegrambot.client;

import org.churk.telegrambot.model.Article;
import org.churk.telegrambot.model.Quote;
import org.churk.telegrambot.model.RedditPost;
import org.churk.telegrambot.model.Shitpost;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "meme-client", url = "${meme-api.url}")
public interface MemeClient {
    @PostMapping(value = "/gpt/prompt", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    ResponseEntity<String> getGpt(@RequestBody String prompt);

    @GetMapping("/meme/reddit/{subreddit}/{count}")
    ResponseEntity<List<RedditPost>> getRedditPost(@PathVariable("subreddit") String subreddit, @PathVariable("count") int count);

    @GetMapping("/meme/shitpost/{search}")
    ResponseEntity<Shitpost> getShitPost(@PathVariable("search") String search);

    @GetMapping("/meme/shitpost")
    ResponseEntity<Shitpost> getShitPost();

    @GetMapping("/meme/shitpost/quote")
    ResponseEntity<Quote> getShitPostQuote();

    @GetMapping("/news/{category}")
    ResponseEntity<List<Article>> getNews(@PathVariable("category") String category);

    @PostMapping(value = "/elevenlabs/tts", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    ResponseEntity<byte[]> getTts(@RequestBody String text);
}
