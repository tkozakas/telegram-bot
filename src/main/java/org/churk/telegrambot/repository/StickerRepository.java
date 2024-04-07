package org.churk.telegrambot.repository;

import org.churk.telegrambot.model.sticker.Sticker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StickerRepository extends JpaRepository<Sticker, UUID> {
    @Query("SELECT s FROM stickers s WHERE s.chatId = ?1 AND s.stickerSetName = ?2")
    List<Sticker> findByChatIdAndStickerSetName(Long chatId, String stickerSetName);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN TRUE ELSE FALSE END FROM stickers s WHERE s.chatId = ?1 AND s.stickerSetName = ?2")
    boolean existsByChatIdAndStickerSetName(Long chatId, String first);

    @Query("SELECT s FROM stickers s WHERE s.chatId = ?1")
    List<Sticker> findAllByChatId(Long chatId);

    @Query("SELECT DISTINCT s.stickerSetName FROM stickers s WHERE s.chatId = ?1")
    List<String> findDistinctStickerSetNamesByChatId(Long chatId);
}
