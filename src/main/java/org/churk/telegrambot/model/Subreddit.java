package org.churk.telegrambot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.UUID;

@Data
@Entity(name = "subreddits")
public class Subreddit {
    @Id
    private UUID subredditId;
    private String name;
}
