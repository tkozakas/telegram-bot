package org.churk.telegrambot.reddit;

import lombok.RequiredArgsConstructor;
import org.churk.telegrambot.handler.Command;
import org.churk.telegrambot.handler.Handler;
import org.churk.telegrambot.utility.HandlerContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.util.List;
import java.util.function.UnaryOperator;

@Component
@RequiredArgsConstructor
public class RedditListHandler extends Handler {
    private final SubredditService subredditService;

    @Override
    public List<Validable> handle(HandlerContext context) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();
        List<Subreddit> subreddits = subredditService.getSubreddits(chatId);

        UnaryOperator<String> escapeMarkdown = name -> name
                .replaceAll("([_\\\\*\\[\\]()~`>#+\\-=|{}.!])", "\\\\$1");

        String message = "*Subreddits:*\n" +
                subreddits.stream()
                        .limit(20)
                        .map(Subreddit::getSubredditName)
                        .map(escapeMarkdown)
                        .reduce("", (a, b) -> a + "- r/" + b + "\n");
        return subreddits.isEmpty() ?
                getReplyMessage(chatId, messageId, "No subreddits available") :
                getMessage(chatId, message);
    }

    @Override
    public Command getSupportedCommand() {
        return Command.REDDIT_LIST;
    }
}
