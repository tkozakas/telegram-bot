package org.churk.telegrambot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("meme")
public class MemeProperties {
    private String apiUrl;
    private String downloadPath;
    private String fileName;
    private String schedule;
}
