package org.churk.telegrambot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@Table(name = "stats")
@NoArgsConstructor
@AllArgsConstructor
public class Stats {
    @Id
    private UUID statsId;
    private Long chatId;
    private Long userId;
    private String firstName;
    private Long score;
    private Integer year;
    private Boolean isWinner;
}
