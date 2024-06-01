package org.churk.telegrambot.builder;

import org.churk.telegrambot.handler.Handler;
import org.churk.telegrambot.utility.UpdateContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.util.List;
import java.util.function.Function;

@Component
public abstract class ListHandler<T> extends Handler {

    protected List<Validable> formatListResponse(UpdateContext context, List<T> items, Function<T, String> itemNameExtractor, String header, String footer, String emptyMessage, boolean isMarkdown) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();
        ListDecorator<T> decorator = new ListDecorator<>(items, itemNameExtractor);

        String message = decorator.getFormattedList(header, footer, 10, isMarkdown);

        return items.isEmpty() ?
                getReplyMessage(chatId, messageId, emptyMessage) :
                getMessageWithMarkdown(chatId, message);
    }
}
