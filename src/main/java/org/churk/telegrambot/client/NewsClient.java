package org.churk.telegrambot.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "news", url = "https://newsapi.org/v2")
public interface NewsClient {
    @GetMapping("/everything?sortBy=popularity&pageSize=10")
    Map<String, Object> getNewsByCategory(@RequestParam("q") String category,
                                          @RequestParam("apiKey") String apiKey,
                                          @RequestParam("from") String from,
                                          @RequestParam("language") String language);
}

