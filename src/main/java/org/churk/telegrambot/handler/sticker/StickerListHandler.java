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
public class StickerListHandler implements CommandHandler {
    private final MessageBuilderFactory messageBuilderFactory;
    private final StickerService stickerService;

    @Override
    public List<Validable> handle(HandlerContext context) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();
        List<String> stickerSets = stickerService.getStickerSetNames(chatId);

        return stickerSets.isEmpty() ?
                getTextMessageWithReply(chatId, messageId, "No sticker sets available") :
                getTextMessage(chatId, stickerSets);
    }

    private List<Validable> getTextMessage(Long chatId, List<String> stickers) {
        String message = "*Sticker sets:*\n" + stickers.stream()
                .limit(20)
                .reduce("", (a, b) -> a + "- " + b + "\n");

        return List.of(messageBuilderFactory
                .createTextMessageBuilder(chatId)
                .withText(message)
                .build());
    }

    private List<Validable> getTextMessageWithReply(Long chatId, Integer messageId, String text) {
        return List.of(messageBuilderFactory
                .createTextMessageBuilder(chatId)
                .withReplyToMessageId(messageId)
                .withText(text)
                .build());
    }

    @Override
    public Command getSupportedCommand() {
        return Command.STICKER_LIST;
    }
}
