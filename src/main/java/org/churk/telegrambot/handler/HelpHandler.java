package org.churk.telegrambot.handler;

import lombok.AllArgsConstructor;
import org.churk.telegrambot.model.Command;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.util.List;

@Component
@AllArgsConstructor
public class HelpHandler implements CommandHandler {

    @Override
    public List<Validable> handle(HandlerContext context) {
        return List.of();
    }

    @Override
    public Command getSupportedCommand() {
        return Command.HELP;
    }
}
