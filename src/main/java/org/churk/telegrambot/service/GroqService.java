package org.churk.telegrambot.service;

import lombok.RequiredArgsConstructor;
import org.churk.telegrambot.client.GroqClient;
import org.churk.telegrambot.config.GroqProperties;
import org.churk.telegrambot.model.GroqRequest;
import org.churk.telegrambot.model.GroqResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class GroqService {
    private final GroqProperties groqProperties;
    private final GroqClient groqClient;
    private final Map<Long, List<GroqRequest.Message>> userSessions = new ConcurrentHashMap<>();

    public GroqResponse getChatCompletion(Long userId, String prompt) {
        GroqRequest request = getGroqRequest(userId, prompt);
        return groqClient.getChatCompletion("Bearer " + groqProperties.getApiKey(), request);
    }

    private GroqRequest getGroqRequest(Long userId, String prompt) {
        List<GroqRequest.Message> messages = userSessions.computeIfAbsent(userId, this::initializeSession);
        messages.add(new GroqRequest.Message("user", prompt));

        return new GroqRequest(
                messages.toArray(new GroqRequest.Message[0]),
                groqProperties.getModel(),
                1,
                groqProperties.getFrequencyPenalty(),
                groqProperties.getMaxTokens(),
                groqProperties.getPresencePenalty(),
                false,
                groqProperties.getTemperature(),
                groqProperties.getTopP(),
                groqProperties.getToolChoice()
        );
    }

    private List<GroqRequest.Message> initializeSession(Long userId) {
        GroqRequest.Message systemMessage = new GroqRequest.Message("system", groqProperties.getSetupPrompt());
        return new ArrayList<>(List.of(systemMessage));
    }

    public void clearSession(Long userId) {
        userSessions.remove(userId);
    }
}
