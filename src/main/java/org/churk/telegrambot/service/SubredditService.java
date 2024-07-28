package org.churk.telegrambot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrambot.model.Subreddit;
import org.churk.telegrambot.repository.SubredditRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubredditService {
    private final SubredditRepository subredditRepository;

    public void addSubreddit(Long chatId, String subreddit) {
        subredditRepository.save(new Subreddit(chatId, subreddit));
    }

    public void deleteSubreddit(Long chatId, String subreddit) {
        subredditRepository.deleteByChatIdAndSubredditName(chatId, subreddit);
    }

    public boolean existsByChatIdAndSubredditName(Long chatId, String subreddit) {
        return subredditRepository.existsByChatIdAndSubredditName(chatId, subreddit);
    }

    public List<Subreddit> getSubreddits(Long chatId) {
        return subredditRepository.findAllByChatId(chatId);
    }
}
