package org.churk.telegrambot.repository;

import org.churk.telegrambot.model.Fact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FactRepository extends JpaRepository<Fact, UUID> {
}
