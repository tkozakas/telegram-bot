package org.churk.telegrambot.handler;

import lombok.AllArgsConstructor;
import org.churk.telegrambot.builder.MessageBuilderFactory;
import org.churk.telegrambot.config.BotProperties;
import org.churk.telegrambot.model.Command;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.util.Arrays;
import java.util.List;

@Component
@AllArgsConstructor
public class HelpHandler implements CommandHandler {
    private final MessageBuilderFactory messageBuilderFactory;
    private final BotProperties botProperties;

    @Override
    public List<Validable> handle(HandlerContext context) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();

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

        return List.of(messageBuilderFactory
                .createTextMessageBuilder(chatId)
                .withText(message)
                .withReplyToMessageId(messageId)
                .build());
    }

    @Override
    public Command getSupportedCommand() {
        return Command.HELP;
    }
}
