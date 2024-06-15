package org.churk.telegrambot.service;

import jakarta.annotation.PostConstruct;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class GroqService {
    private final GroqProperties groqProperties;
    private final GroqClient groqClient;
    private final Map<Long, List<GroqRequest.Message>> userSessions = new ConcurrentHashMap<>();
    private final Map<Long, Long> lastAccessed = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @PostConstruct
    private void scheduleSessionCleanup() {
        scheduler.scheduleAtFixedRate(this::clearInactiveSessions, 1, 1, TimeUnit.HOURS);
    }

    public GroqResponse getChatCompletion(Long chatId, String prompt) {
        return getChatCompletion(chatId, prompt, groqProperties.getMaxTokens());
    }

    public GroqResponse getChatCompletion(Long chatId, String prompt, int maxTokens) {
        updateLastAccessed(chatId);
        GroqRequest request = getGroqRequest(chatId, prompt, maxTokens);
        return groqClient.getChatCompletion("Bearer " + groqProperties.getApiKey(), request);
    }

    private GroqRequest getGroqRequest(Long chatId, String prompt, int maxTokens) {
        List<GroqRequest.Message> messages = userSessions.computeIfAbsent(chatId, this::initializeSession);
        messages.add(new GroqRequest.Message("user", prompt));

        return new GroqRequest(
                messages.toArray(new GroqRequest.Message[0]),
                groqProperties.getModel(),
                1,
                groqProperties.getFrequencyPenalty(),
                maxTokens,
                groqProperties.getPresencePenalty(),
                false,
                groqProperties.getTemperature(),
                groqProperties.getTopP(),
                groqProperties.getToolChoice()
        );
    }

    private List<GroqRequest.Message> initializeSession(Long chatId) {
        GroqRequest.Message systemMessage = new GroqRequest.Message("system", groqProperties.getSetupPrompt());
        return new ArrayList<>(List.of(systemMessage));
    }

    public void clearSession(Long chatId) {
        userSessions.remove(chatId);
        lastAccessed.remove(chatId);
    }

    private void updateLastAccessed(Long userId) {
        lastAccessed.put(userId, System.currentTimeMillis());
    }

    private void clearInactiveSessions() {
        long now = System.currentTimeMillis();
        long timeout = TimeUnit.HOURS.toMillis(1);  // 1 hour timeout

        lastAccessed.entrySet().removeIf(entry -> {
            if (now - entry.getValue() > timeout) {
                userSessions.remove(entry.getKey());
                return true;
            }
            return false;
        });
    }
}
