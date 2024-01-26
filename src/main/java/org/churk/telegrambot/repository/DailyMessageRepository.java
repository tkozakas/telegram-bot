package org.churk.telegrambot.repository;

import org.churk.telegrambot.model.bot.DailyMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DailyMessageRepository extends JpaRepository<DailyMessage, UUID> {
    @Query("SELECT dm FROM DailyMessage dm WHERE dm.keyName = ?1")
    Optional<DailyMessage> findDailyMessageByKeyName(String keyName);
}
