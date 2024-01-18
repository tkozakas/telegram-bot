package org.churk.telegrampibot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrampibot.model.DailyMessage;
import org.churk.telegrampibot.model.Sentence;
import org.churk.telegrampibot.repository.DailyMessageRepository;
import org.churk.telegrampibot.repository.SentenceRepository;
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
        Optional<DailyMessage> dailyMessage = dailyMessageRepository.findDailyMessageByKeyName("sentences");
        if (dailyMessage.isEmpty()) {
            return Collections.emptyList();
        }

        // find one random group id
        List<UUID> groupIds = sentenceRepository.findGroupIdsByDailyMessageId(dailyMessage.get().getDailyMessageId());
        if (groupIds.isEmpty()) {
            return Collections.emptyList();
        }
        UUID randomGroupId = groupIds.get(ThreadLocalRandom.current().nextInt(groupIds.size()));

        // find all sentences for that group id
        List<Sentence> sentences = sentenceRepository.findAllByGroupIdAndDailyMessageId(randomGroupId, dailyMessage.get().getDailyMessageId());
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
