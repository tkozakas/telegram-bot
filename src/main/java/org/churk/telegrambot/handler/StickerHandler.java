package org.churk.telegrambot.handler;

import lombok.AllArgsConstructor;
import org.churk.telegrambot.builder.MessageBuilderFactory;
import org.churk.telegrambot.model.Sticker;
import org.churk.telegrambot.service.StickerService;
import org.telegram.telegrambots.meta.api.interfaces.Validable;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@AllArgsConstructor
public class StickerHandler implements CommandHandler {
    private final MessageBuilderFactory messageBuilderFactory;
    private final StickerService stickerService;
    @Override
    public List<Validable> handle(Update update) {
        List<Validable> response = new ArrayList<>();
        Long chatId = update.getMessage().getChatId();

        List<Sticker> stickers = stickerService.getAllStickers();
        Sticker randomSticker = stickers.get(ThreadLocalRandom.current().nextInt(stickers.size()));
        SendSticker sticker = messageBuilderFactory
                .createStickerMessageBuilder(chatId)
                .withSticker(randomSticker.getFileId())
                .build();

        response.add(sticker);
        return response;
    }
}
