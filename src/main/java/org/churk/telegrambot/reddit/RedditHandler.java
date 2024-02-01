package org.churk.telegrambot.reddit;

import org.churk.telegrambot.builder.MessageBuilderFactory;
import org.churk.telegrambot.config.BotProperties;
import org.churk.telegrambot.handler.Command;
import org.churk.telegrambot.handler.Handler;
import org.churk.telegrambot.message.DailyMessageService;
import org.churk.telegrambot.utility.HandlerContext;
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
        Integer messageId = context.getUpdate().getMessage().getMessageId();
        List<Subreddit> subreddits = subredditService.getSubreddits(chatId);

        String subreddit;
        if (context.getArgs().isEmpty()) {
            if (subreddits.isEmpty()) {
                return getReplyMessage(chatId, messageId,
                        "No subreddits available (use /redditadd <subreddit>)");
            }
            subreddit = subreddits.get(ThreadLocalRandom.current().nextInt(subreddits.size())).getSubredditName();
        } else {
            subreddit = context.getArgs().getFirst();
        }

        if (!subredditService.isValidSubreddit(subreddit)) {
            return getReplyMessage(chatId, messageId,
                    "This subreddit does not exist.");
        }

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
