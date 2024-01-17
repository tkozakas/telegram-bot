package org.churk.telegrampibot.repository;

import org.churk.telegrampibot.model.Fact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FactRepository extends JpaRepository<Fact, Long> {
}
