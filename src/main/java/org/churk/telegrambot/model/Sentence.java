package org.churk.telegrambot.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "sentence")
public class Sentence {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID sentenceId;
    private UUID groupId;
    @Column(length = 1000)
    private String text;
    private int orderNumber;
}
