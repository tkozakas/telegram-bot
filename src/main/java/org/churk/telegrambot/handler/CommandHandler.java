package org.churk.telegrambot.handler;

import org.telegram.telegrambots.meta.api.interfaces.Validable;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public interface CommandHandler {
    List<Validable> handle(Update update);
}
