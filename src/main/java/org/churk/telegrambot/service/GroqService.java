package org.churk.telegrambot.service;

import lombok.RequiredArgsConstructor;
import org.churk.telegrambot.client.GroqClient;
import org.churk.telegrambot.config.GroqProperties;
import org.churk.telegrambot.model.GroqRequest;
import org.churk.telegrambot.model.GroqResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroqService {
    private final GroqProperties groqProperties;
    private final GroqClient groqClient;

    public GroqResponse getChatCompletion(String prompt) {
        GroqRequest request = getGroqRequest(prompt);
        return groqClient.getChatCompletion("Bearer " + groqProperties.getApiKey(), request);
    }

    private GroqRequest getGroqRequest(String prompt) {
        GroqRequest.Message systemMessage = new GroqRequest.Message("system", groqProperties.getSetupPrompt());
        GroqRequest.Message userMessage = new GroqRequest.Message("user", prompt);

        return new GroqRequest(
                new GroqRequest.Message[]{systemMessage, userMessage},
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
}
