package org.churk.telegrambot.utility;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrambot.config.LoaderProperties;
import org.churk.telegrambot.model.Subreddit;
import org.churk.telegrambot.repository.SubredditRepository;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubredditLoader {
    private final LoaderProperties loaderProperties;
    private final SubredditRepository subredditRepository;

    public void loadSubredits() {
        if (!loaderProperties.isLoadSubreddits()) {
            return;
        }
        subredditRepository.deleteAll();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(loaderProperties.getSubredditsPath()));
            bufferedReader.lines().forEach(line -> {
                Subreddit subreddit = new Subreddit();
                subreddit.setSubredditId(UUID.randomUUID());
                subreddit.setName(line);
                subredditRepository.save(subreddit);
            });
        } catch (FileNotFoundException e) {
            log.error("Error while loading subreddits from file: " + loaderProperties.getSubredditsPath(), e);
        }
    }
}
