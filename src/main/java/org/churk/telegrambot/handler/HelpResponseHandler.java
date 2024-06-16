package org.churk.telegrambot.handler;

import lombok.RequiredArgsConstructor;
import org.churk.telegrambot.builder.ListResponseHandler;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.model.UpdateContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class HelpResponseHandler extends ListResponseHandler<Command> {
    @Override
    public List<Validable> handle(UpdateContext context) {
        List<Command> commands = Stream.of(Command.values()).filter(c -> c != Command.NONE).toList();
        context.setMarkdown(true);
        return formatListResponse(context, commands, this::formatCommand,
                "Available commands:\n",
                "",
                "No commands available");
    }

    private String formatCommand(Command command) {
        String commandName = command.getPatterns().getFirst().formatted(botProperties.getWinnerName())
                .replace(".*/", "/")
                .replace("\\b.*", "");
        String subCommands = command.getSubCommandsString();
        return String.format("*%s %s* - %s %n",
                commandName,
                subCommands.isEmpty() ? "" : "<" + subCommands + ">",
                command.getDescription().replace("%s", botProperties.getWinnerName()));
    }

    @Override
    public Command getSupportedCommand() {
        return Command.HELP;
    }
}
