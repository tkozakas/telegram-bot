package org.churk.telegrambot.factory;

import lombok.AllArgsConstructor;
import lombok.Setter;
import org.churk.telegrambot.builder.MessageBuilderFactory;
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

    private final MessageBuilderFactory messageBuilderFactory;
    public CommandHandler getHandler(Command command, List<String> arguments) {
        return switch (command) {
            case DAILY_MESSAGE -> new DailyMessageHandler(messageBuilderFactory, dailyMessageService, statsService);
            case REGISTER -> new RegisterHandler(statsService, arguments);
            case STATS -> new StatsHandler(statsService, arguments);
            case STATS_ALL -> new StatsAllHandler(statsService, arguments);
            case STATS_USER -> new StatsUserHandler(statsService, arguments);
            case FACT -> new FactHandler(factService, arguments);
            case STICKER -> new StickerHandler(stickerService, arguments);
            case MEME -> new RedditHandler(redditService, arguments);
        };
    }
}
