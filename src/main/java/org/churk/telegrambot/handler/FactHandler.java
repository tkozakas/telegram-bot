package org.churk.telegrambot.handler;

import lombok.AllArgsConstructor;
import org.churk.telegrambot.service.FactService;
import org.telegram.telegrambots.meta.api.interfaces.Validable;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@AllArgsConstructor
public class FactHandler implements CommandHandler {
    private final FactService factService;
    private final List<String> arguments;
    @Override
    public List<Validable> handle(Update update) {
        return null;
    }
}
