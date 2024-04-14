package org.churk.telegrambot.handler;

import lombok.RequiredArgsConstructor;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.model.RedditPost;
import org.churk.telegrambot.model.SubCommand;
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
        if (context.getArgs().isEmpty()) {
            return handleRandomPost(context);
        }

        SubCommand subCommand = SubCommand.getSubCommand(context.getArgs().getFirst().toUpperCase());

        if (subCommand == null) {
            return getReplyMessage(context.getUpdate().getMessage().getChatId(),
                    context.getUpdate().getMessage().getMessageId(),
                    "Invalid command, please use /reddit <add/list/remove/random>");
        }

        return switch (subCommand) {
            case ADD -> handleAdd(context);
            case LIST -> handleList(context);
            case REMOVE -> handleRemove(context);
            case RANDOM -> handleRandomPost(context);
            default -> handlePost(context);
        };
    }

    private List<Validable> handleRandomPost(HandlerContext context) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();

        if (subredditService.getSubreddits(chatId).isEmpty()) {
            return getReplyMessage(chatId, messageId,
                    "No subreddits available use /reddit add <subreddit>");
        }

        return handlePost(context);
    }

    private List<Validable> handleAdd(HandlerContext context) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();
        String args = context.getArgs().getLast();

        if (args.isEmpty() || !subredditService.isValidSubreddit(args)) {
            return getReplyMessage(chatId, messageId,
                    "Please provide a valid name /reddit add <subreddit>");
        }
        String subreddit = args;
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
        String args = context.getArgs().getLast();
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();

        if (args.isEmpty() || !subredditService.isValidSubreddit(args)) {
            return getReplyMessage(chatId, messageId,
                    "Please provide a valid name /reddit remove <subreddit>");
        }
        if (!subredditService.existsByChatIdAndSubredditName(chatId, args)) {
            return getReplyMessage(chatId, messageId,
                    "Subreddit " + args + " does not exist in the list");
        }
        subredditService.deleteSubreddit(chatId, args);
        return getReplyMessage(chatId, messageId,
                "Subreddit " + args + " removed");
    }

    private List<Validable> handlePost(HandlerContext context) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();
        String subreddit = chooseSubreddit(context, chatId);

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
