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
public class StickerAddHandler implements CommandHandler {
    private static final String STICKER_SET_URL = "https://t.me/addstickers/";
    private final MessageBuilderFactory messageBuilderFactory;
    private final StickerService stickerService;

    @Override
    public List<Validable> handle(HandlerContext context) {
        String stickerSetName = context.getArgs().getFirst();
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();

        if (stickerSetName.startsWith(STICKER_SET_URL)) {
            stickerSetName = stickerSetName.replace(STICKER_SET_URL, "");
        }
        if (stickerSetName.isEmpty() || !stickerService.isValidSticker(stickerSetName)) {
            return getTextMessageWithReply(chatId, messageId,
                    "Please provide a valid name /stickeradd <sticker_name>");
        }
        if (stickerService.existsByChatIdAndStickerName(chatId, stickerSetName)) {
            return getTextMessageWithReply(chatId, messageId,
                    "Sticker set " + stickerSetName + " already exists in the list");
        }
        stickerService.addSticker(chatId, stickerSetName);
        return getTextMessageWithReply(chatId, messageId,
                "Sticker set " + stickerSetName + " added");
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
        return Command.STICKER_ADD;
    }
}
