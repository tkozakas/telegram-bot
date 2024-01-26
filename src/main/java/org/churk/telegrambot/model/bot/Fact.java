package org.churk.telegrambot.model.bot;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity(name = "facts")
@NoArgsConstructor
public class Fact {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID factId;
    @Column(length = 3500)
    private String comment;
}
