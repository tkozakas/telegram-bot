package org.churk.telegrambot.service;

import lombok.RequiredArgsConstructor;
import org.churk.telegrambot.client.GroqClient;
import org.churk.telegrambot.config.GroqProperties;
import org.churk.telegrambot.model.GroqRequest;
import org.churk.telegrambot.model.GroqResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroqService {
    private final GroqProperties groqProperties;
    private final GroqClient groqClient;

    public String chatWithGroq(String userMessage, String latestReply, String messageHistory) {
        List<GroqRequest.Message> messages = new ArrayList<>();
        messages.add(new GroqRequest.Message("user", userMessage));

        if (!messageHistory.isEmpty()) {
            messages.addFirst(new GroqRequest.Message("system",
                    String.format("Please respond formally and avoid scientific or philosophical topics. Please focus on the conversation before you not the past. Don't write a lot of text (1-3 sentences). " +
                                    "But our conversation's summary so far: \"%s\". And this is the latest reply from you \"%s\". Dont tell us about our past conversation. Just focus on the current one.",
                            messageHistory, latestReply)));
        }

        return getResponse(messages);
    }

    private String getResponse(List<GroqRequest.Message> messages) {
        GroqRequest request = new GroqRequest(
                messages.toArray(new GroqRequest.Message[0]),
                groqProperties.getModel(),
                1,
                groqProperties.getFrequencyPenalty(),
                groqProperties.getMaxTokens(),
                groqProperties.getPresencePenalty(),
                groqProperties.getTemperature(),
                groqProperties.getTopP(),
                groqProperties.getToolChoice()
        );

        GroqResponse response = groqClient.getChatCompletion("Bearer " + groqProperties.getApiKey(), request);
        return response.getChoices().getFirst().getMessage().getContent();
    }
}
