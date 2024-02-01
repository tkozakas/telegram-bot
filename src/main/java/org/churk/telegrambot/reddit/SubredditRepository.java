package org.churk.telegrambot.reddit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubredditRepository extends JpaRepository<Subreddit, UUID> {

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN TRUE ELSE FALSE END FROM subreddits r WHERE r.chatId = ?1 AND r.subredditName = ?2")
    boolean existsByChatIdAndSubredditName(Long chatId, String subredditName);

    @Query("SELECT r FROM subreddits r WHERE r.chatId = ?1")
    List<Subreddit> findAllByChatId(Long chatId);

    @Query("SELECT r FROM subreddits r WHERE r.chatId = ?1 AND r.subredditName = ?2")
    Optional<Subreddit> findByChatIdAndSubredditName(Long chatId, String subreddit);
}
