package org.churk.telegrampibot.repository;

import org.churk.telegrampibot.model.Stats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatsRepository extends JpaRepository<Stats, Long> {

}
