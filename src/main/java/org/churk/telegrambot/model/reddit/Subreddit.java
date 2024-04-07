package org.churk.telegrambot.model.reddit;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@Entity(name = "subreddits")
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class Subreddit {
    private final Long chatId;
    private final String subredditName;
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID subredditId;
}
