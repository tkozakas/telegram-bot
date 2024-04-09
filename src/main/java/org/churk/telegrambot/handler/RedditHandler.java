package org.churk.telegrambot.handler;

import lombok.RequiredArgsConstructor;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.model.RedditPost;
import org.churk.telegrambot.model.Subreddit;
import org.churk.telegrambot.service.SubredditService;
import org.churk.telegrambot.utility.HandlerContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.UnaryOperator;

@Component
@RequiredArgsConstructor
public class RedditHandler extends Handler {
    private static final String REDDIT_URL = "https://www.reddit.com/r/";
    private final SubredditService subredditService;

    @Override
    public List<Validable> handle(HandlerContext context) {
        List<String> args = context.getArgs();
        String subCommand = args.isEmpty() ? "get" : args.getFirst().toLowerCase();

        return switch (subCommand) {
            case "add" -> handleAdd(context);
            case "list" -> handleList(context);
            case "remove" -> handleRemove(context);
            default -> handleGetRandomPost(context);
        };
    }

    private List<Validable> handleAdd(HandlerContext context) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();
        List<String> args = context.getArgs().subList(1, context.getArgs().size());

        if (args.isEmpty() || !subredditService.isValidSubreddit(args.getFirst())) {
            return getReplyMessage(chatId, messageId,
                    "Please provide a valid name /reddit add <subreddit>");
        }
        String subreddit = args.getFirst();
        if (subreddit.startsWith(REDDIT_URL)) {
            subreddit = subreddit.replace(REDDIT_URL, "");
        }
        if (subredditService.existsByChatIdAndSubredditName(chatId, subreddit)) {
            return getReplyMessage(chatId, messageId,
                    "Subreddit %s already exists in the list".formatted(subreddit));
        }
        subredditService.addSubreddit(chatId, subreddit);
        return getReplyMessage(chatId, messageId,
                "Subreddit %s added".formatted(subreddit));
    }

    private List<Validable> handleList(HandlerContext context) {
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
                getMessageWithMarkdown(chatId, message);
    }

    private List<Validable> handleRemove(HandlerContext context) {
        List<String> args = context.getArgs().subList(1, context.getArgs().size());
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();

        if (args.isEmpty() || !subredditService.isValidSubreddit(args.getFirst())) {
            return getReplyMessage(chatId, messageId,
                    "Please provide a valid name /reddit remove <subreddit>");
        }
        if (!subredditService.existsByChatIdAndSubredditName(chatId, args.getFirst())) {
            return getReplyMessage(chatId, messageId,
                    "Subreddit " + args.getFirst() + " does not exist in the list");
        }
        subredditService.deleteSubreddit(chatId, args.getFirst());
        return getReplyMessage(chatId, messageId,
                "Subreddit " + args.getFirst() + " removed");
    }

    private List<Validable> handleGetRandomPost(HandlerContext context) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();
        String subreddit = chooseSubreddit(context, chatId);

        if (subredditService.getSubreddits(chatId).isEmpty()) {
            return List.of();
        }
        return getRedditPost(subreddit, chatId, messageId);
    }

    private List<Validable> getRedditPost(String subreddit, Long chatId, Integer messageId) {
        if (subreddit == null) {
            return getReplyMessage(chatId, messageId,
                    "No subreddits available use /reddit add <subreddit>");
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
