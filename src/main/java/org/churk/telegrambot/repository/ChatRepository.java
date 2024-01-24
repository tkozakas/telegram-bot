package org.churk.telegrambot.repository;

import org.churk.telegrambot.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {
}
