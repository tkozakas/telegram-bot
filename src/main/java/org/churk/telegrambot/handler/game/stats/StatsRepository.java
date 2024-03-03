package org.churk.telegrambot.handler.game.stats;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StatsRepository extends JpaRepository<Stat, UUID> {
    @Query("SELECT s FROM Stat s WHERE s.chatId = ?1 AND s.userId = ?2")
    List<Stat> getStatsByChatIdAndUserId(Long chatId, Long userId);

    @Query("SELECT s FROM Stat s WHERE s.chatId = ?1 AND s.year = ?2")
    List<Stat> getStatsByChatIdAndYear(Long chatId, int year);

    @Query("SELECT s FROM Stat s WHERE s.chatId = ?1")
    List<Stat> findAllByChatId(Long chatId);

    @Query("SELECT s FROM Stat s WHERE s.chatId = ?1 AND s.year = ?2")
    List<Stat> findAllByChatIdAndYear(Long chatId, int year);

    @Query("SELECT s.userId FROM Stat s WHERE s.chatId = ?1 AND s.firstName = ?2")
    Long getUserIdByChatIdAndFirstName(Long chatId, String firstName);
}
