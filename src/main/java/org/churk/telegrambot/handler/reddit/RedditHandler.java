package org.churk.telegrambot.handler.reddit;

import org.churk.telegrambot.builder.MessageBuilderFactory;
import org.churk.telegrambot.config.BotProperties;
import org.churk.telegrambot.handler.Handler;
import org.churk.telegrambot.handler.HandlerContext;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.model.Subreddit;
import org.churk.telegrambot.service.DailyMessageService;
import org.churk.telegrambot.service.SubredditService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class RedditHandler extends Handler {
    private final SubredditService subredditService;

    public RedditHandler(BotProperties botProperties, DailyMessageService dailyMessageService, MessageBuilderFactory messageBuilderFactory, SubredditService subredditService) {
        super(botProperties, dailyMessageService, messageBuilderFactory);
        this.subredditService = subredditService;
    }

    @Override
    public List<Validable> handle(HandlerContext context) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        List<Subreddit> subreddits = subredditService.getSubreddits(chatId);
        Integer messageId = context.getUpdate().getMessage().getMessageId();
        Subreddit randomSubreddit = subreddits.get(ThreadLocalRandom.current().nextInt(subreddits.size()));
        String subreddit = context.getArgs().isEmpty() ?
                randomSubreddit.getSubredditName() :
                context.getArgs().getFirst();

        try {
            Optional<File> file = subredditService.getMemeFromSubreddit(subreddit);
            if (file.isPresent()) {
                File existingFile = file.get();
                existingFile.deleteOnExit();
                String fileName = file.get().getName().toLowerCase();
                return fileName.endsWith(".gif") ?
                        getAnimationMessage(chatId, existingFile, "From r/%s".formatted(subreddit)) :
                        getPhotoMessage(chatId, existingFile, "From r/%s".formatted(subreddit));
            }
        } catch (Exception e) {
            return getReplyMessage(chatId, messageId, "Something went wrong, please try again later");
        }
        return List.of();
    }

    @Override
    public Command getSupportedCommand() {
        return Command.REDDIT;
    }
}
