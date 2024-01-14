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
    public List<Subreddit> getSubreddits() {
        return subredditRepository.findAll();
    }
}
