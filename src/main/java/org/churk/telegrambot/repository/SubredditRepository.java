package org.churk.telegrambot.repository;

import org.churk.telegrambot.model.Subreddit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SubredditRepository extends JpaRepository<Subreddit, UUID> {
}
