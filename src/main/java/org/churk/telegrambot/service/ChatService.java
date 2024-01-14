package org.churk.telegrambot.service;

import lombok.AllArgsConstructor;
import org.churk.telegrambot.model.Chat;
import org.churk.telegrambot.repository.ChatRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;

    public void saveChat(Long chatId, String chatName) {
        chatRepository.save(new Chat(chatId, chatName));
    }

    public List<Chat> getAllChats() {
        return chatRepository.findAll();
    }
}
