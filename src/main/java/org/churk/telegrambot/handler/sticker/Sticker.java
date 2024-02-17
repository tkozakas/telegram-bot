package org.churk.telegrambot.handler.sticker;

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
    private Long chatId;
    private String stickerSetName;
}
