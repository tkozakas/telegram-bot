package org.churk.telegrambot.repository;

import org.churk.telegrambot.model.Sentence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SentenceRepository extends JpaRepository<Sentence, UUID> {

    @Query("SELECT s FROM Sentence s WHERE s.groupId = ?1")
    List<Sentence> findAllByGroupIdAndDailyMessageId(UUID groupId);

    @Query("SELECT s.groupId FROM Sentence s GROUP BY s.groupId, s.orderNumber")
    List<UUID> findGroupIdsByDailyMessageId();
}
