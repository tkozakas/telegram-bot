package org.churk.telegrambot.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrambot.client.ElevenLabsClient;
import org.churk.telegrambot.config.ElevenLabsProperties;
import org.churk.telegrambot.model.TextToSpeechRequest;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class VoiceOverService {
    private final ElevenLabsProperties elevenLabsProperties;
    private ElevenLabsClient elevenLabsClient;

    public Optional<File> getSpeech(String text) {
        String apiKey = elevenLabsProperties.getApiKey();
        String voiceId = elevenLabsProperties.getVoiceId();
        String outputFileName = voiceId + ".wav";

        TextToSpeechRequest request = new TextToSpeechRequest(
                text,
                "eleven_multilingual_v2",
                Map.of(
                        "stability", 0.5,
                        "similarity_boost", 0.8,
                        "style", 0.0,
                        "use_speaker_boost", true
                )
        );
        byte[] audioStream = elevenLabsClient.convertTextToSpeech(apiKey, voiceId, request);
        try (FileOutputStream fos = new FileOutputStream(outputFileName)) {
            fos.write(audioStream);
            log.info("Audio stream saved to file: {}", outputFileName);
            return Optional.of(new File(outputFileName));
        } catch (IOException e) {
            log.error("Error saving audio stream to file", e);
        }
        return Optional.empty();
    }
}
