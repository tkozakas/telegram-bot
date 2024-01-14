package org.churk.telegrambot.factory;

import lombok.AllArgsConstructor;
import lombok.Setter;
import org.churk.telegrambot.builder.MessageBuilderFactory;
import org.churk.telegrambot.config.BotProperties;
import org.churk.telegrambot.handler.*;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.service.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Setter
public class HandlerFactory {
    private final StatsService statsService;
    private final DailyMessageService dailyMessageService;
    private final FactService factService;
    private final RedditService redditService;
    private final StickerService stickerService;
    private final ChatService chatService;

    private final MessageBuilderFactory messageBuilderFactory;
    private final BotProperties botProperties;
    public CommandHandler getHandler(Command command, List<String> arguments) {
        return switch (command) {
            case START -> new StartHandler(messageBuilderFactory, dailyMessageService, chatService);
            case HELP -> new HelpHandler(botProperties, messageBuilderFactory);
            case FACT -> new FactHandler(messageBuilderFactory, factService);
            case STICKER -> new StickerHandler(messageBuilderFactory, stickerService);
            case REDDIT -> new RedditHandler(messageBuilderFactory, redditService, arguments);
            case STATS -> new StatsHandler(messageBuilderFactory, dailyMessageService, statsService, arguments);
            case STATS_ALL -> new StatsAllHandler(messageBuilderFactory, dailyMessageService, statsService);
            case STATS_USER ->
                    new StatsUserHandler(botProperties, messageBuilderFactory, dailyMessageService, statsService);
            case REGISTER -> new RegisterHandler(messageBuilderFactory, dailyMessageService, statsService);
            case DAILY_MESSAGE ->
                    new DailyMessageHandler(botProperties, messageBuilderFactory, dailyMessageService, statsService);
        };
    }
}
