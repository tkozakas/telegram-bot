package org.churk.telegrambot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class GroqRequest {
    private Message[] messages;
    private String model;
    private Integer n; // Number of chat completion choices
    @JsonProperty("frequency_penalty")
    private Double frequencyPenalty;
    @JsonProperty("max_tokens")
    private Integer maxTokens;
    @JsonProperty("presence_penalty")
    private Double presencePenalty;
    private Double temperature;
    @JsonProperty("top_p")
    private Double topP;
    @JsonProperty("tool_choice")
    private Object toolChoice;

    public GroqRequest(Message[] messages, String model, int n, Double frequencyPenalty, int maxTokens, Double presencePenalty, Double temperature, Double topP, String toolChoice) {
        this.messages = messages;
        this.model = model;
        this.n = n;
        this.frequencyPenalty = frequencyPenalty;
        this.maxTokens = maxTokens;
        this.presencePenalty = presencePenalty;
        this.temperature = temperature;
        this.topP = topP;
        this.toolChoice = toolChoice;
    }

    @Data
    @AllArgsConstructor
    public static class Message {
        private String role;
        private String content;
    }
}
