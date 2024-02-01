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
public class RedditRemoveHandler implements CommandHandler {
    private final MessageBuilderFactory messageBuilderFactory;
    private final SubredditService subredditService;

    @Override
    public List<Validable> handle(HandlerContext context) {
        List<String> args = context.getArgs();
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();

        if (args.isEmpty() || !subredditService.isValidSubreddit(args.getFirst())) {
            return getTextMessageWithReply(chatId, messageId,
                    "Please provide a valid name /redditremove <subreddit>");
        }
        if (!subredditService.existsByChatIdAndSubredditName(chatId, args.getFirst())) {
            return getTextMessageWithReply(chatId, messageId,
                    "Subreddit " + args.getFirst() + " does not exist in the list");
        }
        subredditService.deleteSubreddit(chatId, args.getFirst());
        return getTextMessageWithReply(chatId, messageId,
                "Subreddit " + args.getFirst() + " removed");
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
        return Command.REDDIT_REMOVE;
    }
}
