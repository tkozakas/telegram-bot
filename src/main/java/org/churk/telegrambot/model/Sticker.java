package org.churk.telegrambot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.UUID;

@Data
@Entity(name = "stickers")

@JsonIgnoreProperties(ignoreUnknown = true)
public class Sticker {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID stickerId;
    @JsonProperty("file_id")
    private String fileId;
    @JsonProperty("is_animated")
    private Boolean isAnimated;
    @JsonProperty("is_video")
    private Boolean isVideo;
    @JsonProperty("emoji")
    private String emoji;
    @JsonProperty("file_size")
    private Integer fileSize;
}
