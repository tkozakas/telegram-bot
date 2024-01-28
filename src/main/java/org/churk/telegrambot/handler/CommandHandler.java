package org.churk.telegrambot.handler;

import org.churk.telegrambot.model.Command;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.util.List;

public interface CommandHandler {
    List<Validable> handle(HandlerContext context);

    Command getSupportedCommand();
}
