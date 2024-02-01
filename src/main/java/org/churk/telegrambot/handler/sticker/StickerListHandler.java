package org.churk.telegrambot.handler.sticker;

import org.churk.telegrambot.builder.MessageBuilderFactory;
import org.churk.telegrambot.config.BotProperties;
import org.churk.telegrambot.handler.Handler;
import org.churk.telegrambot.handler.HandlerContext;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.service.DailyMessageService;
import org.churk.telegrambot.service.StickerService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.util.List;

@Component
public class StickerListHandler extends Handler {
    private final StickerService stickerService;

    public StickerListHandler(DailyMessageService dailyMessageService, BotProperties botProperties, MessageBuilderFactory messageBuilderFactory, StickerService stickerService) {
        super(dailyMessageService, botProperties, messageBuilderFactory);
        this.stickerService = stickerService;
    }

    @Override
    public List<Validable> handle(HandlerContext context) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();
        List<String> stickerSets = stickerService.getStickerSetNames(chatId);

        String message = "*Sticker sets:*\n" + stickerSets.stream()
                .limit(20)
                .reduce("", (a, b) -> a + "- " + b + "\n");
        return stickerSets.isEmpty() ?
                getReplyMessage(chatId, messageId, "No sticker sets available") :
                getMessage(chatId, message);
    }

    @Override
    public Command getSupportedCommand() {
        return Command.STICKER_LIST;
    }
}
