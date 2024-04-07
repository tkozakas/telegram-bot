package org.churk.telegrambot.handler.game.sentence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SentenceRepository extends JpaRepository<Sentence, UUID> {

    @Query("SELECT s FROM Sentence s WHERE s.groupId = ?1 AND s.dailyMessage.dailyMessageId = ?2")
    List<Sentence> findAllByGroupIdAndDailyMessageId(UUID groupId, UUID dailyMessageId);
    @Query("SELECT s.groupId FROM Sentence s WHERE s.dailyMessage.dailyMessageId = ?1 GROUP BY s.groupId, s.orderNumber")
    List<UUID> findGroupIdsByDailyMessageId(UUID dailyMessageId);
}
