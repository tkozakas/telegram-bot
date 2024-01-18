package org.churk.telegrampibot.repository;

import org.churk.telegrampibot.model.Stats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Stats, Long> {
    @Query("SELECT s FROM Stats s WHERE s.chatId = ?1 AND s.userId = ?2")
    List<Stats> findStatsByChatIdAndUserId(Long chatId, Long userId);

    @Query("SELECT s FROM Stats s WHERE s.chatId = ?1 AND YEAR(s.createdAt) = ?2")
    List<Stats> findStatsByChatIdAndYear(Long chatId, int year);

    @Query("SELECT s.isWinner FROM Stats s WHERE s.isWinner = TRUE")
    boolean existsIsWinner();

    @Query("SELECT s.userId FROM Stats s WHERE s.userId = ?1")
    boolean existsByUserId(Long userId);
}
