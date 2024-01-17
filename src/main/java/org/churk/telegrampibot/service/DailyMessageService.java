package org.churk.telegrampibot.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrampibot.model.DailyMessage;
import org.churk.telegrampibot.model.Sentence;
import org.churk.telegrampibot.repository.DailyMessageRepository;
import org.churk.telegrampibot.repository.SentenceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
public class DailyMessageService {
    private final String jsonPath = "src/main/resources/daily-messages.json";
    private final boolean ENABLED = true;
    private final DailyMessageRepository dailyMessageRepository;
    private final SentenceRepository sentenceRepository;

    public DailyMessageService(DailyMessageRepository dailyMessageRepository, SentenceRepository sentenceRepository) {
        this.dailyMessageRepository = dailyMessageRepository;
        this.sentenceRepository = sentenceRepository;
    }

    public void loadMessages() {
        if (!ENABLED) {
            return;
        }

        sentenceRepository.deleteAll();
        dailyMessageRepository.deleteAll();
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<Object> dataList = mapper.readValue(new File(jsonPath), new TypeReference<>() {
            });

            dataList.stream().filter(dataObject -> dataObject instanceof Map).map(dataObject -> (Map<String, Object>) dataObject).forEachOrdered(dataMap -> {
                DailyMessage dailyMessage = new DailyMessage();
                dailyMessage.setDailyMessageId(UUID.randomUUID());
                if (dataMap.containsKey("key_name")) {
                    dailyMessage.setKeyName((String) dataMap.get("key_name"));
                }

                if (dataMap.containsKey("sentences")) {
                    List<List<String>> sentences = (List<List<String>>) dataMap.get("sentences");
                    for (List<String> strings : sentences) {
                        UUID groupId = UUID.randomUUID();
                        for (String sentenceText : strings) {
                            Sentence sentence = new Sentence();
                            sentence.setGroupId(groupId);
                            sentence.setSentenceId(UUID.randomUUID()); // Generate a new UUID for each sentence
                            sentence.setText(sentenceText);
                            sentence.setDailyMessage(dailyMessage);
                            dailyMessage.getSentences().add(sentence);
                        }
                    }
                }
                dailyMessageRepository.save(dailyMessage);
            });

        } catch (IOException e) {
            log.error("Error while loading JSON file: " + jsonPath, e);
        }
    }

    @Transactional
    public List<Sentence> getRandomGroupSentences() {
        Optional<DailyMessage> dailyMessage = dailyMessageRepository.findNullKeyname();
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
        Optional<DailyMessage> dailyMessage = dailyMessageRepository.findByKeyName(keyName);
        if (dailyMessage.isEmpty()) {
            return "";
        }
        return dailyMessage.get().getKeyName();
    }
}
