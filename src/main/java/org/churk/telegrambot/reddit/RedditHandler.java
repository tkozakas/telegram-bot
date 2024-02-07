package org.churk.telegrambot.reddit;

import lombok.RequiredArgsConstructor;
import org.churk.telegrambot.handler.Command;
import org.churk.telegrambot.handler.Handler;
import org.churk.telegrambot.utility.HandlerContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
public class RedditHandler extends Handler {
    private final SubredditService subredditService;

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
            Optional<RedditPost> redditPost = subredditService.getMemeFromSubreddit(subreddit);
            if (redditPost.isPresent()) {
                RedditPost post = redditPost.get();
                File existingFile = subredditService.getFile(post).join().get();
                existingFile.deleteOnExit();
                String fileName = existingFile.getName().toLowerCase();
                String caption = (post.getTitle() != null ? post.getTitle() + "\n" : "") +
                        " From r/%s".formatted(subreddit);
                return fileName.endsWith(".gif") ?
                        getAnimation(chatId, existingFile, caption) :
                        getPhoto(chatId, existingFile, caption);
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
