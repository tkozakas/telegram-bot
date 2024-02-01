package org.churk.telegrambot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrambot.client.TelegramClient;
import org.churk.telegrambot.config.BotProperties;
import org.churk.telegrambot.model.Sticker;
import org.churk.telegrambot.model.StickerResponse;
import org.churk.telegrambot.repository.StickerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class StickerService {
    private final BotProperties botConfig;
    private final TelegramClient telegramClient;
    private final StickerRepository stickerRepository;

    public List<Sticker> getStickerSets(Long chatId) {
        return stickerRepository.findAllByChatId(chatId);
    }

    public List<String> getStickerSetNames(Long chatId) {
        return stickerRepository.findDistinctStickerSetNamesByChatId(chatId);
    }

    public void addSticker(Long chatId, String stickerSetName) {
        StickerResponse stickerSet = getStickerSet(stickerSetName).orElseThrow();
        stickerSet.getResult().getStickers().forEach(sticker -> {
            sticker.setChatId(chatId);
            sticker.setStickerSetName(stickerSetName);
        });
        stickerRepository.saveAll(stickerSet.getResult().getStickers());
    }

    public void deleteSticker(Long chatId, String stickerSetName) {
        stickerRepository.deleteAll(stickerRepository.findByChatIdAndStickerSetName(chatId, stickerSetName));
    }

    public boolean isValidSticker(String stickerSetName) {
        return getStickerSet(stickerSetName).isPresent();
    }

    public boolean existsByChatIdAndStickerName(Long chatId, String first) {
        return stickerRepository.existsByChatIdAndStickerSetName(chatId, first);
    }

    private Optional<StickerResponse> getStickerSet(String stickerSetName) {
        String botToken = botConfig.getToken();
        try {
            String response = telegramClient.getStickerSet(botToken, stickerSetName);
            ObjectMapper mapper = new ObjectMapper();
            return Optional.of(mapper.readValue(response, StickerResponse.class));
        } catch (FeignException e) {
            log.error("Error with Feign client", e);
        } catch (JsonProcessingException e) {
            log.error("Error with parsing JSON", e);
        }
        return Optional.empty();
    }
}
