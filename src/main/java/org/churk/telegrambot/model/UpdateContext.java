package org.churk.telegrambot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.churk.telegrambot.handler.HandlerFactory;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public final class UpdateContext {
    private Command command;
    private List<String> args;
    private HandlerFactory handlerFactory;
    private Update update;
    private boolean isReply;
    private boolean isMarkdown;
}
