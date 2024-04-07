package org.churk.telegrambot.model.reddit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RedditPost {
    private String url;
    private String title;
}
