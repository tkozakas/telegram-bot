package org.churk.telegrambot.handler;

import org.churk.telegrambot.builder.MessageBuilderFactory;
import org.churk.telegrambot.config.BotProperties;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.service.DailyMessageService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.util.Arrays;
import java.util.List;

@Component
public class HelpHandler extends Handler {

    public HelpHandler(BotProperties botProperties, DailyMessageService dailyMessageService, MessageBuilderFactory messageBuilderFactory) {
        super(botProperties, dailyMessageService, messageBuilderFactory);
    }

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

        return getReplyMessage(chatId, messageId, message);
    }

    @Override
    public Command getSupportedCommand() {
        return Command.HELP;
    }
}
