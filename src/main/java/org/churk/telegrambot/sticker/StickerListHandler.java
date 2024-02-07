package org.churk.telegrambot.sticker;

import lombok.RequiredArgsConstructor;
import org.churk.telegrambot.handler.Command;
import org.churk.telegrambot.handler.Handler;
import org.churk.telegrambot.utility.HandlerContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.util.List;
import java.util.function.UnaryOperator;

@Component
@RequiredArgsConstructor
public class StickerListHandler extends Handler {
    private final StickerService stickerService;

    @Override
    public List<Validable> handle(HandlerContext context) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();
        List<String> stickerSets = stickerService.getStickerSetNames(chatId);

        UnaryOperator<String> escapeMarkdown = name -> name
                .replaceAll("([_\\\\*\\[\\]()~`>#+\\-=|{}.!])", "\\\\$1");

        String message = "*Sticker sets:*\n" + stickerSets.stream()
                .limit(20)
                .map(escapeMarkdown)
                .reduce("", (a, b) -> a + "- " + b + "\n");
        return stickerSets.isEmpty() ?
                getReplyMessage(chatId, messageId, "No sticker sets available") :
                getMessageWithMarkdown(chatId, message);
    }

    @Override
    public Command getSupportedCommand() {
        return Command.STICKER_LIST;
    }
}
