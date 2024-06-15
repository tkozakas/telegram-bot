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
                    String.format("Our conversation's summary so far: \"%s\". And this is the latest reply from you \"%s\"",
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