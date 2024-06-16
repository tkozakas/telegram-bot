package org.churk.telegrambot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrambot.model.DailyMessage;
import org.churk.telegrambot.model.Sentence;
import org.churk.telegrambot.repository.DailyMessageRepository;
import org.churk.telegrambot.repository.SentenceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyMessageService {
    private final DailyMessageRepository dailyMessageRepository;
    private final SentenceRepository sentenceRepository;

    @Transactional
    public List<Sentence> getRandomGroupSentences() {
        List<UUID> groupIds = sentenceRepository.findGroupIdsByDailyMessageId();
        if (groupIds.isEmpty()) {
            return Collections.emptyList();
        }
        UUID randomGroupId = groupIds.get(ThreadLocalRandom.current().nextInt(groupIds.size()));

        List<Sentence> sentences = sentenceRepository.findAllByGroupIdAndDailyMessageId(randomGroupId);
        if (sentences.isEmpty()) {
            return Collections.emptyList();
        }
        return sentences;
    }


    public String getKeyNameSentence(String keyName) {
        Optional<DailyMessage> dailyMessage = dailyMessageRepository.findDailyMessageByKeyName(keyName);
        if (dailyMessage.isEmpty()) {
            log.error("Key name not found: " + keyName);
            return "";
        }
        return dailyMessage.get().getText();
    }
}
