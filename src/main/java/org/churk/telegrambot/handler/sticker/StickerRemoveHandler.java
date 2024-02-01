package org.churk.telegrambot.handler.sticker;

import lombok.AllArgsConstructor;
import org.churk.telegrambot.builder.MessageBuilderFactory;
import org.churk.telegrambot.handler.CommandHandler;
import org.churk.telegrambot.handler.HandlerContext;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.service.StickerService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.util.List;

@Component
@AllArgsConstructor
public class StickerRemoveHandler implements CommandHandler {
    private final MessageBuilderFactory messageBuilderFactory;
    private final StickerService stickerService;

    @Override
    public List<Validable> handle(HandlerContext context) {
        List<String> args = context.getArgs();
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();

        if (args.isEmpty() || !stickerService.isValidSticker(args.getFirst())) {
            return getTextMessageWithReply(chatId, messageId,
                    "Please provide a valid name /stickerremove <sticker_name>");
        }
        if (!stickerService.existsByChatIdAndStickerName(chatId, args.getFirst())) {
            return getTextMessageWithReply(chatId, messageId,
                    "Sticker set " + args.getFirst() + " does not exist in the list");
        }
        stickerService.deleteSticker(chatId, args.getFirst());
        return getTextMessageWithReply(chatId, messageId,
                "Sticker set " + args.getFirst() + " removed");
    }

    private List<Validable> getTextMessageWithReply(Long chatId, Integer messageId, String s) {
        return List.of(messageBuilderFactory
                .createTextMessageBuilder(chatId)
                .withReplyToMessageId(messageId)
                .withText(s)
                .enableMarkdown(false)
                .build());
    }

    @Override
    public Command getSupportedCommand() {
        return Command.STICKER_REMOVE;
    }
}
