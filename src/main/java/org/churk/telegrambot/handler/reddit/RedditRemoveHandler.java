package org.churk.telegrambot.handler.reddit;

import org.churk.telegrambot.builder.MessageBuilderFactory;
import org.churk.telegrambot.config.BotProperties;
import org.churk.telegrambot.handler.Handler;
import org.churk.telegrambot.handler.HandlerContext;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.service.DailyMessageService;
import org.churk.telegrambot.service.SubredditService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.util.List;

@Component
public class RedditRemoveHandler extends Handler {
    private final SubredditService subredditService;

    public RedditRemoveHandler(BotProperties botProperties, DailyMessageService dailyMessageService, MessageBuilderFactory messageBuilderFactory, SubredditService subredditService) {
        super(botProperties, dailyMessageService, messageBuilderFactory);
        this.subredditService = subredditService;
    }

    @Override
    public List<Validable> handle(HandlerContext context) {
        List<String> args = context.getArgs();
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();

        if (args.isEmpty() || !subredditService.isValidSubreddit(args.getFirst())) {
            return getReplyMessage(chatId, messageId,
                    "Please provide a valid name /redditremove <subreddit>");
        }
        if (!subredditService.existsByChatIdAndSubredditName(chatId, args.getFirst())) {
            return getReplyMessage(chatId, messageId,
                    "Subreddit " + args.getFirst() + " does not exist in the list");
        }
        subredditService.deleteSubreddit(chatId, args.getFirst());
        return getReplyMessage(chatId, messageId,
                "Subreddit " + args.getFirst() + " removed");
    }

    @Override
    public Command getSupportedCommand() {
        return Command.REDDIT_REMOVE;
    }
}
