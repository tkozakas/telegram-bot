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
public class Stat {
    @Id
    private UUID statsId;
    private Long userId;
    private Long chatId;
    private String firstName;
    private Long score;
    private Integer year;
    private Boolean isWinner;
}
