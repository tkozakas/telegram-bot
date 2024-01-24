package org.churk.telegrambot.handler;

import lombok.AllArgsConstructor;
import org.churk.telegrambot.builder.MessageBuilderFactory;
import org.churk.telegrambot.model.Sticker;
import org.churk.telegrambot.service.StickerService;
import org.telegram.telegrambots.meta.api.interfaces.Validable;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@AllArgsConstructor
public class StickerHandler implements CommandHandler {
    private final MessageBuilderFactory messageBuilderFactory;
    private final StickerService stickerService;
    @Override
    public List<Validable> handle(Update update) {
        Long chatId = update.getMessage().getChatId();
        return getSticker(chatId);
    }

    @Override
    public List<Validable> handleByChatId(Long chatId) {
        return getSticker(chatId);
    }

    private List<Validable> getSticker(Long chatId) {
        List<Sticker> stickers = stickerService.getAllStickers();
        Sticker randomSticker = stickers.get(ThreadLocalRandom.current().nextInt(stickers.size()));

        return List.of(messageBuilderFactory
                .createStickerMessageBuilder(chatId)
                .withSticker(randomSticker.getFileId())
                .build());
    }
}
