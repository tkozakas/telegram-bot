package org.churk.telegrambot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "news")
public class NewsProperties {
    private String apiKey;
    private String language;
}
