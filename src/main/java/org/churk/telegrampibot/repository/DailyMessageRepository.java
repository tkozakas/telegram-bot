package org.churk.telegrampibot.repository;

import org.churk.telegrampibot.model.DailyMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DailyMessageRepository extends JpaRepository<DailyMessage, Long> {
    @Query("SELECT dm FROM DailyMessage dm WHERE dm.keyName = ?1")
    Optional<DailyMessage> findDailyMessageByKeyName(String keyName);
}
