package org.churk.telegrambot.news;

import lombok.Data;

import java.util.List;

@Data
public class NewsResponse {
    private String status;
    private int totalResults;
    private List<Article> articles;
}
