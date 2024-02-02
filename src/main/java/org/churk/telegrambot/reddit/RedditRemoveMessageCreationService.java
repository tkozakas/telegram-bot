package org.churk.telegrambot.reddit;

import lombok.RequiredArgsConstructor;
import org.churk.telegrambot.handler.Command;
import org.churk.telegrambot.handler.MessageCreationService;
import org.churk.telegrambot.utility.HandlerContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RedditRemoveMessageCreationService extends MessageCreationService {
    private final SubredditService subredditService;

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
