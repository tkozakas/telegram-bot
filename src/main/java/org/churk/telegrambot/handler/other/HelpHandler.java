package org.churk.telegrambot.handler.other;

import lombok.RequiredArgsConstructor;
import org.churk.telegrambot.handler.Command;
import org.churk.telegrambot.handler.Handler;
import org.churk.telegrambot.utility.HandlerContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HelpHandler extends Handler {
    @Override
    public List<Validable> handle(HandlerContext context) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        String message = Arrays.stream(Command.values())
                .map(command ->
                        "*" + command.getPatterns().toString().formatted(botProperties.getWinnerName())
                                .replace("[", "")
                                .replace("]", "")
                                .replace(".*/", "/")
                                .replace("\\b.*", "") + "* - " +
                                command.getDescription().formatted(botProperties.getWinnerName()))
                .reduce((s1, s2) -> s1 + "\n" + s2)
                .orElse("No commands available");

        return getMessageWithMarkdown(chatId, message);
    }

    @Override
    public Command getSupportedCommand() {
        return Command.HELP;
    }
}
