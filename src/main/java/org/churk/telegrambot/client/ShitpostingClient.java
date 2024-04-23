package org.churk.telegrambot.client;

import org.churk.telegrambot.model.Quote;
import org.churk.telegrambot.model.Shitpost;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "shitpostingClient", url = "https://api.thedailyshitpost.net")
public interface ShitpostingClient {
    @GetMapping("/random")
    Shitpost getShitpost();

    @GetMapping("/search")
    Shitpost getShitpost(@RequestParam("search") String search);

    @GetMapping("/quote/random")
    Quote getQuote();
}
