package org.churk.telegrambot.utility;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrambot.client.TelegramClient;
import org.churk.telegrambot.config.BotProperties;
import org.churk.telegrambot.config.LoaderProperties;
import org.churk.telegrambot.model.Sticker;
import org.churk.telegrambot.repository.StickerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StickerLoader {
    private final LoaderProperties loaderProperties;
    private final BotProperties botConfig;
    private final TelegramClient telegramClient;
    private final StickerRepository stickerRepository;

    public void loadStickers() {
        if (!loaderProperties.isLoadStickers()) {
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

}
