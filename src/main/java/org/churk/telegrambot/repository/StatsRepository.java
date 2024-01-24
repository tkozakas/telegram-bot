package org.churk.telegrambot.repository;

import org.churk.telegrambot.model.Stats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StatsRepository extends JpaRepository<Stats, UUID> {
    @Query("SELECT s FROM Stats s WHERE s.chatId = ?1 AND s.userId = ?2")
    Optional<Stats> getStatsByChatIdAndUserId(Long chatId, Long userId);

    @Query("SELECT s FROM Stats s WHERE s.chatId = ?1 AND s.year = ?2")
    List<Stats> getStatsByChatIdAndYear(Long chatId, int year);

    @Query("SELECT s FROM Stats s WHERE s.chatId = ?1")
    List<Stats> findAllByChatId(Long chatId);

    @Query("SELECT s FROM Stats s WHERE s.chatId = ?1 AND s.year = ?2")
    List<Stats> findAllByChatIdAndYear(Long chatId, int year);
}
