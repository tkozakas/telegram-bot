package org.churk.telegrambot.sticker;

import lombok.RequiredArgsConstructor;
import org.churk.telegrambot.handler.Command;
import org.churk.telegrambot.handler.Handler;
import org.churk.telegrambot.utility.HandlerContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StickerAddHandler extends Handler {
    private static final String STICKER_SET_URL = "https://t.me/addstickers/";
    private final StickerService stickerService;

    @Override
    public List<Validable> handle(HandlerContext context) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();

        String stickerSetName = context.getArgs().getFirst();
        if (stickerSetName.startsWith(STICKER_SET_URL)) {
            stickerSetName = stickerSetName.replace(STICKER_SET_URL, "");
        }
        if (context.getArgs().isEmpty() || !stickerService.isValidSticker(stickerSetName)) {
            return getReplyMessage(chatId, messageId,
                    "Please provide a valid name /stickeradd <name>");
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
