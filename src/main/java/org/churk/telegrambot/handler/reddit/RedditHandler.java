package org.churk.telegrambot.handler.reddit;

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

    public List<Validable> handle(HandlerContext context) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();
        String subreddit = chooseSubreddit(context, chatId);

        if (subreddit == null) {
            return getReplyMessage(chatId, messageId,
                    "No subreddits available use /redditadd <subreddit>");
        }
        if (!subredditService.isValidSubreddit(subreddit)) {
            return getReplyMessage(chatId, messageId,
                    "This subreddit does not exist");
        }
        return fetchAndProcessMeme(chatId, messageId, subreddit);
    }

    private String chooseSubreddit(HandlerContext context, Long chatId) {
        List<Subreddit> subreddits = subredditService.getSubreddits(chatId);
        if (context.getArgs().isEmpty()) {
            return subreddits.isEmpty() ? null :
                    subreddits.get(ThreadLocalRandom.current().nextInt(subreddits.size())).getSubredditName();
        } else {
            return context.getArgs().getFirst();
        }
    }

    private List<Validable> fetchAndProcessMeme(Long chatId, Integer messageId, String subreddit) {
        try {
            Optional<RedditPost> redditPost = subredditService.getMemeFromSubreddit(subreddit);
            if (redditPost.isPresent()) {
                RedditPost post = redditPost.get();
                Optional<File> file = subredditService.getFile(post).join();
                if (file.isEmpty()) {
                    return postWithoutFileResponse(chatId, post, subreddit);
                }
                return postWithFileResponse(chatId, post, file.get(), subreddit);
            }
        } catch (Exception e) {
            getReplyMessage(chatId, messageId, "Something went wrong, please try again later");
        }
        return getReplyMessage(chatId, messageId, "Something went wrong, please try again later");
    }

    private List<Validable> postWithoutFileResponse(Long chatId, RedditPost post, String subreddit) {
        String caption = (post.getTitle() != null ? post.getTitle() + "\n" : "") + "<Image unavailable>\n" + "From r/" + subreddit;
        return getMessage(chatId, caption);
    }

    private List<Validable> postWithFileResponse(Long chatId, RedditPost post, File file, String subreddit) {
        file.deleteOnExit();
        String caption = (post.getTitle() != null ? post.getTitle() + "\n" : "") + "From r/" + subreddit;
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".gif") ?
                getAnimation(chatId, file, caption) :
                getPhoto(chatId, file, caption);
    }

    @Override
    public Command getSupportedCommand() {
        return Command.REDDIT;
    }
}
