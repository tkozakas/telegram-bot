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
public class StickerAddHandler extends Handler {
    private static final String STICKER_SET_URL = "https://t.me/addstickers/";
    private final StickerService stickerService;

    public StickerAddHandler(BotProperties botProperties, DailyMessageService dailyMessageService, MessageBuilderFactory messageBuilderFactory, StickerService stickerService) {
        super(botProperties, dailyMessageService, messageBuilderFactory);
        this.stickerService = stickerService;
    }

    @Override
    public List<Validable> handle(HandlerContext context) {
        String stickerSetName = context.getArgs().getFirst();
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();

        if (stickerSetName.startsWith(STICKER_SET_URL)) {
            stickerSetName = stickerSetName.replace(STICKER_SET_URL, "");
        }
        if (stickerSetName.isEmpty() || !stickerService.isValidSticker(stickerSetName)) {
            return getReplyMessage(chatId, messageId,
                    "Please provide a valid name /stickeradd <sticker_name>");
        }
        if (stickerService.existsByChatIdAndStickerName(chatId, stickerSetName)) {
            return getReplyMessage(chatId, messageId,
                    "Sticker set " + stickerSetName + " already exists in the list");
        }
        stickerService.addSticker(chatId, stickerSetName);
        return getReplyMessage(chatId, messageId,
                "Sticker set " + stickerSetName + " added");
    }

    @Override
    public Command getSupportedCommand() {
        return Command.STICKER_ADD;
    }
}
