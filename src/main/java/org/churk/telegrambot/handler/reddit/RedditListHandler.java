package org.churk.telegrambot.handler.reddit;

import lombok.AllArgsConstructor;
import org.churk.telegrambot.builder.MessageBuilderFactory;
import org.churk.telegrambot.handler.CommandHandler;
import org.churk.telegrambot.handler.HandlerContext;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.model.Subreddit;
import org.churk.telegrambot.service.SubredditService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.util.List;

@Component
@AllArgsConstructor
public class RedditListHandler implements CommandHandler {
    private final MessageBuilderFactory messageBuilderFactory;
    private final SubredditService subredditService;

    @Override
    public List<Validable> handle(HandlerContext context) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();
        List<Subreddit> subreddits = subredditService.getSubreddits(chatId);

        return subreddits.isEmpty() ?
                getTextMessageWithReply(chatId, messageId, "No subreddits available") :
                getTextMessage(chatId, subreddits);
    }

    private List<Validable> getTextMessage(Long chatId, List<Subreddit> subreddits) {
        String message = "*Subreddits:*\n" +
                subreddits.stream()
                        .limit(20)
                        .map(Subreddit::getSubredditName)
                        .reduce("", (a, b) -> a + "- /r" + b + "\n");

        return List.of(messageBuilderFactory
                .createTextMessageBuilder(chatId)
                .withText(message)
                .build());
    }

    private List<Validable> getTextMessageWithReply(Long chatId, Integer messageId, String text) {
        return List.of(messageBuilderFactory
                .createTextMessageBuilder(chatId)
                .withReplyToMessageId(messageId)
                .withText(text)
                .build());
    }

    @Override
    public Command getSupportedCommand() {
        return Command.REDDIT_LIST;
    }
}
