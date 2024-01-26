package org.churk.telegrambot.repository;

import org.churk.telegrambot.model.bot.Sticker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StickerRepository extends JpaRepository<Sticker, UUID> {
}
