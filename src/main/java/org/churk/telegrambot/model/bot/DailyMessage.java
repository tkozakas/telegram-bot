package org.churk.telegrambot.model.bot;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "daily_message")
@ToString(exclude = "sentences")
public class DailyMessage {
    @Id
    @GeneratedValue (strategy = GenerationType.UUID)
    private UUID dailyMessageId;

    private String keyName;

    @Column(length = 2500)
    private String text;

    @OneToMany(mappedBy = "dailyMessage", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Sentence> sentences = new ArrayList<>();
}
