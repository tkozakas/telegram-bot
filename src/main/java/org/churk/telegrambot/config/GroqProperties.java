package org.churk.telegrambot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "groq")
public class GroqProperties {
    private String apiKey;
    private String model;
    private String setupPrompt;
    private Integer maxTokens = 200;
    private Double temperature = 0.5;
    private Double topP = 0.86;
    private Double frequencyPenalty = 0.4;
    private Double presencePenalty = 0.4;
    private String toolChoice = "auto";
}
