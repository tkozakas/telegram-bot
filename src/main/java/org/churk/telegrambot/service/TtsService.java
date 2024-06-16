package org.churk.telegrambot.service;

import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrambot.client.ElevenLabsClient;
import org.churk.telegrambot.config.ElevenLabsProperties;
import org.churk.telegrambot.model.TextToSpeechRequest;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class TtsService {
    private final ElevenLabsProperties elevenLabsProperties;
    private ElevenLabsClient elevenLabsClient;

    public Optional<File> getSpeech(String text) throws FeignException {
        List<String> apiKey = elevenLabsProperties.getApiKey();
        String voiceId = elevenLabsProperties.getVoiceId();
        String outputFileName = voiceId + ".wav";

        String prompt = removeUnnecessaryCharacters(text);
        TextToSpeechRequest request = new TextToSpeechRequest(
                prompt,
                "eleven_multilingual_v2",
                Map.of(
                        "stability", 0.5,
                        "similarity_boost", 0.8,
                        "style", 0.0,
                        "use_speaker_boost", true
                )
        );
        for (String key : apiKey) {
            try {
                byte[] audioStream = elevenLabsClient.convertTextToSpeech(key, voiceId, request);
                if (audioStream != null) {
                    try (FileOutputStream fos = new FileOutputStream(outputFileName)) {
                        fos.write(audioStream);
                        log.info("Audio stream saved to file: {}", outputFileName);
                        return Optional.of(new File(outputFileName));
                    } catch (IOException e) {
                        log.error("Error saving audio stream to file", e);
                    }
                }
            } catch (FeignException e) {
                log.error("Error while converting text to speech for api-key: {}", key, e);
            }
        }
        return Optional.empty();
    }

    private String removeUnnecessaryCharacters(String text) {
        return text.replaceAll("\\[.*?\\]", "")
                .replaceAll("\\(.*?\\)", "")
                .replaceAll("https?://\\S+\\s?", "")
                .replaceAll("www.\\S+\\s?", "")
                .replaceAll("\\s+", " ")
                .trim();
    }
}
