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
public class RedditAddHandler extends Handler {
    private static final String REDDIT_URL = "https://www.reddit.com/r/";
    private final SubredditService subredditService;

    public RedditAddHandler(BotProperties botProperties, DailyMessageService dailyMessageService, MessageBuilderFactory messageBuilderFactory, SubredditService subredditService) {
        super(botProperties, dailyMessageService, messageBuilderFactory);
        this.subredditService = subredditService;
    }

    @Override
    public List<Validable> handle(HandlerContext context) {
        String subreddit = context.getArgs().getFirst();
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();

        if (subreddit.startsWith(REDDIT_URL)) {
            subreddit = subreddit.replace(REDDIT_URL, "");
        }
        if (subreddit.isEmpty() || !subredditService.isValidSubreddit(subreddit)) {
            return getReplyMessage(chatId, messageId,
                    "Please provide a valid name /redditadd <subreddit>");
        }
        if (subredditService.existsByChatIdAndSubredditName(chatId, subreddit)) {
            return getReplyMessage(chatId, messageId,
                    "Subreddit " + subreddit + " already exists in the list");
        }
        subredditService.addSubreddit(chatId, subreddit);
        return getReplyMessage(chatId, messageId,
                "Subreddit " + subreddit + " added");
    }

    @Override
    public Command getSupportedCommand() {
        return Command.REDDIT_ADD;
    }
}
