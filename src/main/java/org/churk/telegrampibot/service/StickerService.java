package org.churk.telegrampibot.service;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrampibot.client.TelegramClient;
import org.churk.telegrampibot.config.BotConfig;
import org.churk.telegrampibot.model.Sticker;
import org.churk.telegrampibot.repository.StickerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;


@Slf4j
@Service
public class StickerService {
    private final BotConfig botConfig;
    private final TelegramClient telegramClient;
    private final StickerRepository stickerRepository;
    private final boolean ENABLED = true;

    public StickerService(BotConfig botConfig, TelegramClient telegramClient, StickerRepository stickerRepository) {
        this.botConfig = botConfig;
        this.telegramClient = telegramClient;
        this.stickerRepository = stickerRepository;
    }

    public void loadStickers() {
        if (!ENABLED) {
            return;
        }
        stickerRepository.deleteAll();
        String botToken = botConfig.getToken();
        botConfig.getStickerSets().forEach(stickerSetName -> {
            try {
                Map<String, Object> responseData = telegramClient.getStickerSet(botToken, stickerSetName);
                if (responseData != null && (boolean) responseData.get("ok")) {
                    Map<String, Object> result = (Map<String, Object>) responseData.get("result");
                    List<Map<String, Object>> stickers = (List<Map<String, Object>>) result.get("stickers");

                    stickers.forEach(sticker -> {
                        Sticker stickerEntity = new Sticker();
                        stickerEntity.setStickerId(UUID.randomUUID());
                        stickerEntity.setFileId((String) sticker.get("file_id"));
                        stickerEntity.setSetName(stickerSetName);
                        stickerEntity.setIsAnimated((Boolean) sticker.get("is_animated"));
                        stickerEntity.setIsVideo((Boolean) sticker.get("is_video"));
                        stickerEntity.setEmoji((String) sticker.get("emoji"));
                        stickerEntity.setFileSize((Integer) sticker.get("file_size"));
                        stickerRepository.save(stickerEntity);
                    });
                }
            } catch (FeignException e) {
                log.error("Error while loading stickers from set: " + stickerSetName, e);
            }
        });
    }

    public String getRandomStickerId() {
        List<Sticker> stickers = stickerRepository.findAll();
        int randomIndex = ThreadLocalRandom.current().nextInt(stickers.size());
        return stickers.get(randomIndex).getFileId();
    }
}
