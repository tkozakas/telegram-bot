package org.churk.telegrambot.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrambot.client.ElevenLabsClient;
import org.churk.telegrambot.model.TextToSpeechRequest;
import org.churk.telegrambot.utility.TtsApiKeyManager;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class TtsService {
    private final TtsApiKeyManager ttsApiKeyManager;
    private ElevenLabsClient elevenLabsClient;

    public Optional<File> getSpeech(String text) {
        String apiKey = ttsApiKeyManager.getApiKey();
        String voiceId = ttsApiKeyManager.getVoiceId();
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
        try {
            byte[] audioStream = elevenLabsClient.convertTextToSpeech(apiKey, voiceId, request);
            try (FileOutputStream fos = new FileOutputStream(outputFileName)) {
                fos.write(audioStream);
                log.info("Audio stream saved to file: {}", outputFileName);
                return Optional.of(new File(outputFileName));
            } catch (IOException e) {
                log.error("Error saving audio stream to file", e);
            }
        } catch (Exception e) {
            log.error("Error generating speech", e);
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
