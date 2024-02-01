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
public class StickerRemoveHandler extends Handler {
    private final StickerService stickerService;

    public StickerRemoveHandler(BotProperties botProperties, DailyMessageService dailyMessageService, MessageBuilderFactory messageBuilderFactory, StickerService stickerService) {
        super(botProperties, dailyMessageService, messageBuilderFactory);
        this.stickerService = stickerService;
    }

    @Override
    public List<Validable> handle(HandlerContext context) {
        List<String> args = context.getArgs();
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();

        if (args.isEmpty() || !stickerService.isValidSticker(args.getFirst())) {
            return getReplyMessage(chatId, messageId,
                    "Please provide a valid name /stickerremove <sticker_name>");
        }
        if (!stickerService.existsByChatIdAndStickerName(chatId, args.getFirst())) {
            return getReplyMessage(chatId, messageId,
                    "Sticker set " + args.getFirst() + " does not exist in the list");
        }
        stickerService.deleteSticker(chatId, args.getFirst());
        return getReplyMessage(chatId, messageId,
                "Sticker set " + args.getFirst() + " removed");
    }
    @Override
    public Command getSupportedCommand() {
        return Command.STICKER_REMOVE;
    }
}
