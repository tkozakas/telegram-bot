package org.churk.telegrambot.handler.sticker;

import org.churk.telegrambot.builder.MessageBuilderFactory;
import org.churk.telegrambot.config.BotProperties;
import org.churk.telegrambot.handler.Handler;
import org.churk.telegrambot.handler.HandlerContext;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.model.Sticker;
import org.churk.telegrambot.service.DailyMessageService;
import org.churk.telegrambot.service.StickerService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class StickerHandler extends Handler {
    private final StickerService stickerService;

    public StickerHandler(BotProperties botProperties, DailyMessageService dailyMessageService, MessageBuilderFactory messageBuilderFactory, StickerService stickerService) {
        super(botProperties, dailyMessageService, messageBuilderFactory);
        this.stickerService = stickerService;
    }

    @Override
    public List<Validable> handle(HandlerContext context) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        List<Sticker> stickers = stickerService.getStickerSets(chatId);
        Integer messageId = context.getUpdate().getMessage().getMessageId();
        if (stickers.isEmpty()) {
            return getReplyMessage(chatId, messageId,
                    "No sticker sets available");
        }
        Sticker randomSticker = stickers.get(ThreadLocalRandom.current().nextInt(stickers.size()));
        return context.isReply() ?
                getReplySticker(chatId, randomSticker, messageId) :
                getStickerMessage(chatId, randomSticker);
    }

    @Override
    public Command getSupportedCommand() {
        return Command.STICKER;
    }
}
