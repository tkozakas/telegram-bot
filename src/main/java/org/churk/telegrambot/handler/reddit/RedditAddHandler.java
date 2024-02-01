package org.churk.telegrambot.handler.reddit;

import lombok.AllArgsConstructor;
import org.churk.telegrambot.builder.MessageBuilderFactory;
import org.churk.telegrambot.handler.CommandHandler;
import org.churk.telegrambot.handler.HandlerContext;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.service.SubredditService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.util.List;

@Component
@AllArgsConstructor
public class RedditAddHandler implements CommandHandler {
    private static final String REDDIT_URL = "https://www.reddit.com/r/";
    private final MessageBuilderFactory messageBuilderFactory;
    private final SubredditService subredditService;

    @Override
    public List<Validable> handle(HandlerContext context) {
        String subreddit = context.getArgs().getFirst();
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();
        if (subreddit.startsWith(REDDIT_URL)) {
            subreddit = subreddit.replace(REDDIT_URL, "");
        }
        if (subreddit.isEmpty() || !subredditService.isValidSubreddit(subreddit)) {
            return getTextMessageWithReply(chatId, messageId,
                    "Please provide a valid name /redditadd <subreddit>");
        }
        if (subredditService.existsByChatIdAndSubredditName(chatId, subreddit)) {
            return getTextMessageWithReply(chatId, messageId,
                    "Subreddit " + subreddit + " already exists in the list");
        }
        subredditService.addSubreddit(chatId, subreddit);
        return getTextMessageWithReply(chatId, messageId,
                "Subreddit " + subreddit + " added");
    }

    private List<Validable> getTextMessageWithReply(Long chatId, Integer messageId, String s) {
        return List.of(messageBuilderFactory
                .createTextMessageBuilder(chatId)
                .withReplyToMessageId(messageId)
                .withText(s)
                .enableMarkdown(false)
                .build());
    }

    @Override
    public Command getSupportedCommand() {
        return Command.REDDIT_ADD;
    }
}
