package org.churk.telegrambot.model.bot;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.UUID;

@Data
@Entity(name = "stickers")
public class Sticker {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID stickerId;
    private String fileId;
    private String setName;
    private Boolean isAnimated;
    private Boolean isVideo;
    private String emoji;
    private Integer fileSize;
}
