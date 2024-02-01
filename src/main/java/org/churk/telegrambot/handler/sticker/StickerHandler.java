package org.churk.telegrambot.handler.sticker;

import lombok.AllArgsConstructor;
import org.churk.telegrambot.builder.MessageBuilderFactory;
import org.churk.telegrambot.handler.CommandHandler;
import org.churk.telegrambot.handler.HandlerContext;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.model.Sticker;
import org.churk.telegrambot.service.StickerService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
@AllArgsConstructor
public class StickerHandler implements CommandHandler {
    private final MessageBuilderFactory messageBuilderFactory;
    private final StickerService stickerService;

    @Override
    public List<Validable> handle(HandlerContext context) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        List<Sticker> stickers = stickerService.getStickerSets(chatId);
        Integer messageId = context.getUpdate().getMessage().getMessageId();
        if (stickers.isEmpty()) {
            return List.of(messageBuilderFactory
                    .createTextMessageBuilder(chatId)
                    .withReplyToMessageId(messageId)
                    .withText("No sticker sets available")
                    .build());
        }
        Sticker randomSticker = stickers.get(ThreadLocalRandom.current().nextInt(stickers.size()));
        return context.isReply() ?
                getStickerReply(chatId, randomSticker, messageId) :
                getStickerMessage(chatId, randomSticker);
    }

    private List<Validable> getStickerMessage(Long chatId, Sticker randomSticker) {
        return List.of(messageBuilderFactory
                .createStickerMessageBuilder(chatId)
                .withSticker(randomSticker.getFileId())
                .build());
    }

    private List<Validable> getStickerReply(Long chatId, Sticker randomSticker, Integer messageId) {
        return List.of(messageBuilderFactory
                .createStickerMessageBuilder(chatId)
                .withSticker(randomSticker.getFileId())
                .withReplyToMessageId(messageId)
                .build());
    }

    @Override
    public Command getSupportedCommand() {
        return Command.STICKER;
    }
}
