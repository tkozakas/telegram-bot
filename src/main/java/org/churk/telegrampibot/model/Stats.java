package org.churk.telegrampibot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "stats")
@NoArgsConstructor
public class Stats {
    @Id
    private Long userId;
    private String firstName;
    private Long chatId;
    private Long score;
    private LocalDateTime createdAt;

    public Stats(Long userId, String firstName, Long chatId, Long score, LocalDateTime createdAt) {
        this.userId = userId;
        this.firstName = firstName;
        this.chatId = chatId;
        this.score = score;
        this.createdAt = createdAt;
    }
}
