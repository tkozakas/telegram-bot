package org.churk.telegrambot.model;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Chat {
    @Id
    private Long chatId;
    private String chatName;
}
