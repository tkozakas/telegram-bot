package org.churk.telegrambot.handler;

import lombok.RequiredArgsConstructor;
import org.churk.telegrambot.model.Command;
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
                .map(command -> {
                    String commandName = command.getPatterns().getFirst().formatted(botProperties.getWinnerName())
                            .replace(".*/", "/")
                            .replace("\\b.*", "");
                    String subCommands = command.getSubCommandsString();
                    return String.format("*%s %s* - %s",
                            commandName,
                            subCommands.isEmpty() ? "" : "<" + subCommands + ">",
                            command.getDescription().replace("%s", botProperties.getWinnerName()));
                })
                .reduce((s1, s2) -> s1 + "\n" + s2)
                .orElse("No commands available");

        return getMessageWithMarkdown(chatId, message);
    }

    @Override
    public Command getSupportedCommand() {
        return Command.HELP;
    }
}
