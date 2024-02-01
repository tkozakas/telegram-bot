package org.churk.telegrambot.stats;

import jakarta.persistence.*;
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
    @GeneratedValue (strategy = GenerationType.UUID)
    private UUID statsId;
    private Long userId;
    private Long chatId;
    private String firstName;
    private Integer year;
    private Long score;
    private Boolean isWinner;

    public Stat(String firstName, Long score) {
        this.firstName = firstName;
        this.score = score;
    }
}
