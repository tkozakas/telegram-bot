package org.churk.telegrambot.repository;

import org.churk.telegrambot.model.Stat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface StatsRepository extends JpaRepository<Stat, UUID> {
    @Query("SELECT s FROM Stat s WHERE s.chatId = ?1 AND s.userId = ?2 ORDER BY s.score DESC")
    List<Stat> getStatsByChatIdAndUserId(Long chatId, Long userId);

    @Query("SELECT s FROM Stat s WHERE s.chatId = ?1 AND s.year = ?2 ORDER BY s.score DESC")
    List<Stat> getStatsByChatIdAndYear(Long chatId, int year);

    @Query("SELECT s FROM Stat s WHERE s.chatId = ?1 ORDER BY s.score DESC")
    List<Stat> findAllByChatId(Long chatId);

    @Query("SELECT s FROM Stat s WHERE s.chatId = ?1 AND s.year = ?2 ORDER BY s.score DESC")
    List<Stat> findAllByChatIdAndYear(Long chatId, int year);

    @Query("SELECT s FROM Stat s WHERE s.chatId = ?1 AND s.firstName = ?2 ORDER BY s.score DESC")
    List<Stat> getUserIdByChatIdAndFirstName(Long chatId, String firstName);

    @Modifying
    @Transactional
    @Query("UPDATE Stat s SET s.isWinner = true, s.score = s.score + 1 " +
            "WHERE s.chatId = ?1 AND s.userId = ?2 AND s.year = ?3")
    void setIsWinnerByUserIdAndYear(Long chatId, Long userId, int year);
}
