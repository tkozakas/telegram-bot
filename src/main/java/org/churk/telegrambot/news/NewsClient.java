package org.churk.telegrambot.news;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "newsClient", url = "https://newsapi.org/v2")
public interface NewsClient {
    @GetMapping("/everything?sortBy=popularity")
    Map<String, Object> getNewsByCategory(@RequestParam("q") String category,
                                          @RequestParam("apiKey") String apiKey,
                                          @RequestParam("from") String from,
                                          @RequestParam("language") String language);
}

