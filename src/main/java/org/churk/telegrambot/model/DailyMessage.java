package org.churk.telegrambot.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "daily_message")
public class DailyMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID dailyMessageId;
    private String keyName;
    @Column(length = 2500)
    private String text;
}
