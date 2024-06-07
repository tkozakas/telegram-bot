package org.churk.telegrambot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Article {
    private String title;
    private String url;
    private String urlToImage;
}
