package org.churk.telegrampibot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("meme")
public class MemeConfig {
    private String apiUrl;
    private String downloadPath;
    private String fileName;
}
