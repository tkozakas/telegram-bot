package org.churk.telegrambot.repository;

import org.churk.telegrambot.model.Subreddit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubredditRepository extends JpaRepository<Subreddit, Long> {
}
