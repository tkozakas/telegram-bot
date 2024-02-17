package org.churk.telegrambot.handler.sticker;

import lombok.RequiredArgsConstructor;
import org.churk.telegrambot.handler.Command;
import org.churk.telegrambot.handler.Handler;
import org.churk.telegrambot.utility.HandlerContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StickerRemoveHandler extends Handler {
    private final StickerService stickerService;

    @Override
    public List<Validable> handle(HandlerContext context) {
        List<String> args = context.getArgs();
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();

        if (args.isEmpty() || !stickerService.isValidSticker(args.getFirst())) {
            return getReplyMessage(chatId, messageId,
                    "Please provide a valid name /stickerremove <name>");
        }
        if (!stickerService.existsByChatIdAndStickerName(chatId, args.getFirst())) {
            return getReplyMessage(chatId, messageId,
                    "Sticker set " + args.getFirst() + " does not exist in the list");
        }
        stickerService.deleteSticker(chatId, args.getFirst());
        return getReplyMessage(chatId, messageId,
                "Sticker set " + args.getFirst()+ " removed");
    }
    @Override
    public Command getSupportedCommand() {
        return Command.STICKER_REMOVE;
    }
}
