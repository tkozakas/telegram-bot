package org.churk.telegrambot.client;

import org.churk.telegrambot.model.Quote;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "shitpostingClient", url = "https://api.thedailyshitpost.net")
public interface ShitpostingClient {
    @GetMapping("/random")
    Map<String, Object> getShitpost();

    @GetMapping("/random")
    Map<String, Object> getShitpost(@RequestParam("search") String search);

    @GetMapping("/quote/random")
    Quote getQuote();
}
