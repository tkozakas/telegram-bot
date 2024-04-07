package org.churk.telegrambot.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@Entity(name = "facts")
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class Fact {
    @Column(length = 3500)
    private final Long chatId;
    private final String comment;
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID factId;
}
