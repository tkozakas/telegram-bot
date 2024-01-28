package org.churk.telegrambot.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.churk.telegrambot.client.NewsClient;
import org.churk.telegrambot.config.NewsProperties;
import org.churk.telegrambot.model.news.Article;
import org.churk.telegrambot.model.news.NewsResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class NewsService {
    private final NewsProperties newsProperties;
    private final NewsClient newsClient;

    public List<Article> getNewsByCategory(String country) {
        return getArticles(country);
    }

    private List<Article> getArticles(String category) {
        String categoryQuery = "q=+" + category;
        String apiKey = newsProperties.getApiKey();
        String language = newsProperties.getLanguage();
        NewsResponse newsResponse;
        int days = 31;
        do {
            LocalDateTime from = LocalDateTime.now().minusDays(days);
            String formattedDate = from.format(DateTimeFormatter.ISO_LOCAL_DATE);
            Map<String, Object> jsonObject = newsClient.getNewsByCategory(categoryQuery, apiKey, formattedDate, language);
            newsResponse = new ObjectMapper().convertValue(jsonObject, new TypeReference<>() {
            });
            days--;
        } while (!(newsResponse.getTotalResults() > 0) && days >= 0);

        return newsResponse.getArticles();
    }
}

