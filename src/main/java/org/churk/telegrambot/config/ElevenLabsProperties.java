package org.churk.telegrambot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties("elevenlabs")
public class ElevenLabsProperties {
    private List<String> apiKey;
    private String voiceId;
}
