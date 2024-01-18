package org.churk.telegrambot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "stats")
@NoArgsConstructor
public class Stats {
    @Id
    private UUID statsId;
    private Long chatId;
    private Long userId;
    private String firstName;
    private Long score;
    private LocalDateTime createdAt;
    private Boolean isWinner;

    public Stats(UUID uuid, Long chatId, Long id, String firstName, long l, LocalDateTime now, Boolean isWinner) {
        this.statsId = uuid;
        this.chatId = chatId;
        this.userId = id;
        this.firstName = firstName;
        this.score = l;
        this.createdAt = now;
        this.isWinner = Boolean.FALSE;
    }
}
