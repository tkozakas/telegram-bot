package org.churk.telegrambot.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrambot.client.TelegramClient;
import org.churk.telegrambot.config.BotProperties;
import org.churk.telegrambot.model.stickerset.StickerResponse;
import org.churk.telegrambot.repository.StickerRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StickerLoader {
    private final BotProperties botConfig;
    private final TelegramClient telegramClient;
    private final StickerRepository stickerRepository;

    public void loadStickers() {
        stickerRepository.deleteAll();
        String botToken = botConfig.getToken();
        botConfig.getStickerSetNames().forEach(line -> {
            try {
                String response = telegramClient.getStickerSet(botToken, line);
                ObjectMapper mapper = new ObjectMapper();
                StickerResponse stickerResponse = mapper.readValue(response, StickerResponse.class);
                stickerRepository.saveAll(stickerResponse.getResult().getStickers());
            } catch (FeignException e) {
                log.error("Error while loading stickers from set: " + line, e);
            } catch (JsonProcessingException e) {
                log.error("Error while parsing reddit response", e);
                throw new RuntimeException(e);
            }
        });
    }

}
