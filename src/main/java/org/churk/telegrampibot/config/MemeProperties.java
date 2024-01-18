package org.churk.telegrampibot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties("meme")
public class MemeProperties {
    private String apiUrl;
    private String downloadPath;
    private String fileName;
    private String schedule;
    private List<String> scheduledSubreddits;
}
