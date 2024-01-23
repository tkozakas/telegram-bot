package org.churk.telegrambot.handler;

import lombok.AllArgsConstructor;
import org.churk.telegrambot.service.StatsService;
import org.telegram.telegrambots.meta.api.interfaces.Validable;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@AllArgsConstructor
public class StatsAllHandler implements CommandHandler {
    private final StatsService statsService;
    private final List<String> arguments;
    @Override
    public List<Validable> handle(Update update) {
        return null;
    }
}
