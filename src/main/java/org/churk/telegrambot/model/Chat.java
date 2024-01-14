package org.churk.telegrambot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity(name = "chats")
@NoArgsConstructor
@AllArgsConstructor
public class Chat {
    @Id
    private Long chatId;
    private String chatName;
}
