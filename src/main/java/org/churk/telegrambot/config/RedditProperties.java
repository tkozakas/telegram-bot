package org.churk.telegrambot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("reddit")
public class RedditProperties {
    private String apiUrl;
    private String downloadPath;
    private String fileName;
    private String schedule;
}
