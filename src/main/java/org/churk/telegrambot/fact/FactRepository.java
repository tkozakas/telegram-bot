package org.churk.telegrambot.fact;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FactRepository extends JpaRepository<Fact, UUID> {
}
