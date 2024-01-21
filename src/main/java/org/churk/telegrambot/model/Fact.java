package org.churk.telegrambot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity(name = "facts")
@NoArgsConstructor
public class Fact {
    @Id
    private UUID factId;
    @Column(length = 3500)
    private String comment;
}
