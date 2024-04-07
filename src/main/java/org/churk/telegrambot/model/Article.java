package org.churk.telegrambot.model;

import lombok.Data;

@Data
public class Article {
    private String title;
    private String url;
    private String urlToImage;
}
