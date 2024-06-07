package org.churk.telegrambot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Data
@RequiredArgsConstructor
public class GroqRequest {
    private Message[] messages;
    private String model;
    private Integer n; // Number of chat completion choices
    @JsonProperty("frequency_penalty")
    private Double frequencyPenalty;
    @JsonProperty("logit_bias")
    private Map<String, Integer> logitBias;
    private Boolean logprobs;
    @JsonProperty("max_tokens")
    private Integer maxTokens;
    @JsonProperty("presence_penalty")
    private Double presencePenalty;
    @JsonProperty("response_format")
    private Map<String, Object> responseFormat;
    private Integer seed;
    private String[] stop;
    private Boolean stream;
    private Double temperature;
    @JsonProperty("tool_choice")
    private Object toolChoice;
    private Tool[] tools;
    @JsonProperty("top_logprobs")
    private Integer topLogprobs;
    @JsonProperty("top_p")
    private Double topP;
    private String user;

    public GroqRequest(Message[] messages, String model, int i, Double frequencyPenalty, Integer maxTokens, Double presencePenalty, boolean b, Double temperature, Double topP, String toolChoice) {
        this.messages = messages;
        this.model = model;
        this.n = i;
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

    @Data
    @AllArgsConstructor
    public static class Function {
        private String name;
        private String description;
        private Parameter[] parameters;

        @Data
        @AllArgsConstructor
        public static class Parameter {
            private String name;
            private String type;
        }
    }

    @Data
    @AllArgsConstructor
    public static class Tool {
        private String type;
        private Function function;
    }
}
