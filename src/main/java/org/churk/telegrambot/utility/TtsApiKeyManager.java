package org.churk.telegrambot.utility;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.churk.telegrambot.config.ElevenLabsProperties;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Component
@RequiredArgsConstructor
public class TtsApiKeyManager {
    private final ElevenLabsProperties elevenLabsProperties;
    private final AtomicInteger index = new AtomicInteger(0);

    public String getApiKey() {
        if (elevenLabsProperties.getApiKey().isEmpty()) {
            return null;
        }
        return elevenLabsProperties.getApiKey()
                .get(index.getAndIncrement() % elevenLabsProperties.getApiKey().size());
    }

    public String getVoiceId() {
        return elevenLabsProperties.getVoiceId();
    }
}
