package org.churk.telegrambot.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;

@Data
@Entity
@Table(name = "sentence")
@ToString(exclude = "dailyMessage")
public class Sentence {
    @Id
    @GeneratedValue (strategy = GenerationType.UUID)
    private UUID sentenceId;
    private UUID groupId;
    @Column(length = 1000)
    private String text;
    private int orderNumber;
    @ManyToOne
    @JoinColumn(name = "daily_message_id", nullable = false)
    private DailyMessage dailyMessage;
}
