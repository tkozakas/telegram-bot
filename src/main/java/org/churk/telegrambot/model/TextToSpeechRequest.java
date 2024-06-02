package org.churk.telegrambot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class TextToSpeechRequest {
    private final String text;
    @JsonProperty("model_id")
    private final String modelId;
    @JsonProperty("voice_settings")
    private final Map<String, Object> voiceSettings;
}
