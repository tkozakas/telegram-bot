package org.churk.telegrambot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("elevenlabs")
public class ElevenLabsProperties {
    private String apiKey;
    private String voiceId;
}
