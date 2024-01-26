package org.churk.telegrambot.model.instagram;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Shortcode {
    @JsonProperty("is_video")
    private boolean isVideo;
    @JsonProperty("video_url")
    private String videoUrl;
}
