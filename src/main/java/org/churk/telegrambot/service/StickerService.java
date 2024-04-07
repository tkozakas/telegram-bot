package org.churk.telegrambot.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrambot.client.StickerClient;
import org.churk.telegrambot.config.BotProperties;
import org.churk.telegrambot.model.Sticker;
import org.churk.telegrambot.repository.StickerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class StickerService {
    private final BotProperties botConfig;
    private final StickerClient stickerClient;
    private final StickerRepository stickerRepository;

    public List<Sticker> getStickerSets(Long chatId) {
        return stickerRepository.findAllByChatId(chatId);
    }

    public List<String> getStickerSetNames(Long chatId) {
        return stickerRepository.findDistinctStickerSetNamesByChatId(chatId);
    }

    public void addSticker(Long chatId, String stickerSetName) {
        List<Sticker> stickerSet = getStickerSet(chatId, stickerSetName);
        stickerRepository.saveAll(stickerSet);
    }

    public void deleteSticker(Long chatId, String stickerSetName) {
        stickerRepository.deleteAll(stickerRepository.findByChatIdAndStickerSetName(chatId, stickerSetName));
    }

    public boolean isValidSticker(String stickerSetName) {
        return !getStickerSet(0L, stickerSetName).isEmpty();
    }

    public boolean existsByChatIdAndStickerName(Long chatId, String first) {
        return stickerRepository.existsByChatIdAndStickerSetName(chatId, first);
    }

    private List<Sticker> getStickerSet(Long chatId, String stickerSetName) {
        String botToken = botConfig.getToken();
        try {
            Map<String, Object> response = stickerClient.getStickerSet(botToken, stickerSetName);
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> responseMap = mapper.convertValue(response.get("result"), new TypeReference<>() {
            });
            if (responseMap.containsKey("stickers") && responseMap.get("stickers") instanceof List) {
                List<Map<String, Object>> stickersMapList = (List<Map<String, Object>>) responseMap.get("stickers");
                return stickersMapList.stream()
                        .map(stickerMap -> {
                            Sticker sticker = mapper.convertValue(stickerMap, Sticker.class);
                            sticker.setChatId(chatId);
                            sticker.setStickerSetName(stickerSetName);
                            return sticker;
                        })
                        .toList();
            }
        } catch (FeignException e) {
            log.error("Error with Feign client", e);
        }
        return List.of();
    }

}
