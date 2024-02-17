package org.churk.telegrambot.handler.game;

import lombok.RequiredArgsConstructor;
import org.churk.telegrambot.handler.Command;
import org.churk.telegrambot.handler.Handler;
import org.churk.telegrambot.utility.HandlerContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StartHandler extends Handler {
    @Override
    public List<Validable> handle(HandlerContext context) {
        return List.of();
    }

    @Override
    public Command getSupportedCommand() {
        return Command.START;
    }
}
