package org.churk.telegrambot.reddit;

import org.churk.telegrambot.builder.MessageBuilderFactory;
import org.churk.telegrambot.config.BotProperties;
import org.churk.telegrambot.handler.Handler;
import org.churk.telegrambot.utility.HandlerContext;
import org.churk.telegrambot.handler.Command;
import org.churk.telegrambot.message.DailyMessageService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.util.List;

@Component
public class RedditListHandler extends Handler {
    private final SubredditService subredditService;

    public RedditListHandler(BotProperties botProperties, DailyMessageService dailyMessageService, MessageBuilderFactory messageBuilderFactory, SubredditService subredditService) {
        super(botProperties, dailyMessageService, messageBuilderFactory);
        this.subredditService = subredditService;
    }

    @Override
    public List<Validable> handle(HandlerContext context) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();
        List<Subreddit> subreddits = subredditService.getSubreddits(chatId);

        String message = "*Subreddits:*\n" +
                subreddits.stream()
                        .limit(20)
                        .map(Subreddit::getSubredditName)
                        .reduce("", (a, b) -> a + "- /r" + b + "\n");

        return subreddits.isEmpty() ?
                getReplyMessage(chatId, messageId, "No subreddits available") :
                getMessage(chatId, message);
    }

    @Override
    public Command getSupportedCommand() {
        return Command.REDDIT_LIST;
    }
}
