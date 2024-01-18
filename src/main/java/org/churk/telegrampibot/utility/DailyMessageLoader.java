package org.churk.telegrampibot.utility;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrampibot.config.LoaderProperties;
import org.churk.telegrampibot.model.DailyMessage;
import org.churk.telegrampibot.model.Sentence;
import org.churk.telegrampibot.repository.DailyMessageRepository;
import org.churk.telegrampibot.repository.SentenceRepository;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyMessageLoader {
    private static final String JSON_PATH = "src/main/resources/daily-messages.json";
    private final LoaderProperties loaderProperties;
    private final DailyMessageRepository dailyMessageRepository;
    private final SentenceRepository sentenceRepository;

    public void loadMessages() {
        if (!loaderProperties.isLoadDailyMessages()) {
            return;
        }

        sentenceRepository.deleteAll();
        dailyMessageRepository.deleteAll();
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<Object> dataList = mapper.readValue(new File(JSON_PATH), new TypeReference<>() {
            });

            dataList.stream().filter(Map.class::isInstance).map(dataObject -> (Map<String, Object>) dataObject).forEachOrdered(dataMap -> {
                DailyMessage dailyMessage = new DailyMessage();
                dailyMessage.setDailyMessageId(UUID.randomUUID());
                // iterate over all keys in the map and put inside dailyMessage all keys that they belong to
                dataMap.forEach((key, value) -> {
                    dailyMessage.setKeyName(key);
                    dailyMessage.setText(value.toString());
                });
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
            log.error("Error while loading JSON file: " + JSON_PATH, e);
        }
    }
}
