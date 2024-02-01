package org.churk.telegrambot.chat;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Update;

@Data
@Entity(name = "chats")
@NoArgsConstructor
@AllArgsConstructor
public class Chat {
    @Id
    private Long chatId;
    private Update update;
}
