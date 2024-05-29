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
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RedditHandler extends Handler {
    private static final String REDDIT_URL = "https://www.reddit.com/r/";
    private final SubredditService subredditService;

    @Override
    public List<Validable> handle(HandlerContext context) {
        List<String> args = context.getArgs();

        if (args.isEmpty()) {
            return handleRandomPost(context, 1);
        }
        SubCommand subCommand = SubCommand.getSubCommand(args.getFirst().toLowerCase());
        return switch (subCommand) {
            case ADD -> handleAdd(context);
            case LIST -> handleList(context);
            case REMOVE -> handleRemove(context);
            case RANDOM -> {
                int count = parseCount(context);
                yield handleRandomPost(context, count);
            }
            case NONE -> {
                String firstArg = context.getArgs().getFirst();
                if (isNumeric(firstArg)) {
                    int count = Integer.parseInt(firstArg);
                    yield handleRandomPost(context, count);
                }
                yield handlePost(context, firstArg, 1);
            }
            default -> throw new IllegalStateException("Unexpected value: " + subCommand);
        };
    }

    private List<Validable> handleRandomPost(HandlerContext context, int count) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();

        if (subredditService.getSubreddits(chatId).isEmpty()) {
            return getReplyMessage(chatId, messageId,
                    "No subreddits available. Use %s <%s>"
                            .formatted(Command.REDDIT.getPatternCleaned(), SubCommand.ADD.getCommand().getFirst()));
        }
        String subreddit = chooseSubreddit(chatId);
        return handlePost(context, subreddit, count);
    }

    private List<Validable> handleAdd(HandlerContext context) {
        String args = context.getArgs().getLast();
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();

        if (context.getArgs().size() != 2 || !subredditService.isValidSubreddit(args)) {
            return getReplyMessage(chatId, messageId,
                    "Please provide a valid name %s %s <subreddit>"
                            .formatted(Command.REDDIT.getPatternCleaned(botProperties.getWinnerName()), SubCommand.ADD.getCommand().getFirst()));
        }
        String subreddit = args.startsWith(REDDIT_URL) ? args.replace(REDDIT_URL, "") : args;
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
                getMessageWithMarkdown(chatId, "- r/" + message);
    }

    private List<Validable> handleRemove(HandlerContext context) {
        String args = context.getArgs().getLast();
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();

        if (context.getArgs().size() != 2 || !subredditService.isValidSubreddit(args)) {
            return getReplyMessage(chatId, messageId,
                    "Please provide a valid name %s %s <subreddit>"
                            .formatted(Command.REDDIT.getPatternCleaned(botProperties.getWinnerName()), SubCommand.REMOVE.getCommand().getFirst()));
        }
        if (!subredditService.existsByChatIdAndSubredditName(chatId, args)) {
            return getReplyMessage(chatId, messageId,
                    "Subreddit %s does not exist in the list".formatted(args));
        }
        subredditService.deleteSubreddit(chatId, args);
        return getReplyMessage(chatId, messageId,
                "Subreddit %s removed".formatted(args));
    }

    private List<Validable> handlePost(HandlerContext context, String subreddit, int count) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();
        return getRedditPosts(subreddit, chatId, messageId, count);
    }

    private List<Validable> getRedditPosts(String subreddit, Long chatId, Integer messageId, int count) {
        if (!subredditService.isValidSubreddit(subreddit)) {
            return getReplyMessage(chatId, messageId,
                    "This subreddit does not exist");
        }
        List<RedditPost> posts = subredditService.getRedditPosts(subreddit, count);
        if (posts.isEmpty()) {
            return getReplyMessage(chatId, messageId,
                    "No posts available in r/%s".formatted(subreddit));
        }
        return fetchAndProcessMemes(chatId, messageId, posts, subreddit);
    }

    private List<Validable> fetchAndProcessMemes(Long chatId, Integer messageId, List<RedditPost> posts, String subreddit) {
        return posts.stream()
                .map(post -> fetchAndProcessMeme(chatId, messageId, post, subreddit))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private String chooseSubreddit(Long chatId) {
        List<Subreddit> subreddits = subredditService.getSubreddits(chatId);
        return subreddits.isEmpty() ? null :
                subreddits.get(ThreadLocalRandom.current().nextInt(subreddits.size())).getSubredditName();
    }

    private List<Validable> fetchAndProcessMeme(Long chatId, Integer messageId, RedditPost post, String subreddit) {
        try {
            Optional<File> file = subredditService.getFile(post);

            if (file.isEmpty()) {
                return postWithoutFileResponse(chatId, post, subreddit);
            }
            file.get().deleteOnExit();
            return postWithFileResponse(chatId, post, file.get(), subreddit);
        } catch (Exception e) {
            return getReplyMessage(chatId, messageId, "Something went wrong, please try again later");
        }
    }

    private List<Validable> postWithoutFileResponse(Long chatId, RedditPost post, String subreddit) {
        String caption = "%s%n<Image unavailable>%nFrom r/%s"
                .formatted(post.getTitle() != null ? post.getTitle() : "", subreddit);
        return getMessage(chatId, caption);
    }

    private List<Validable> postWithFileResponse(Long chatId, RedditPost post, File file, String subreddit) {
        file.deleteOnExit();
        String caption = "%s%nFrom r/%s"
                .formatted(post.getTitle() != null ? post.getTitle() : "", subreddit);
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".gif") ?
                getAnimation(chatId, file, caption) :
                getPhoto(chatId, file, caption);
    }

    private int parseCount(HandlerContext context) {
        if (context.getArgs().size() > 1) {
            try {
                return Integer.parseInt(context.getArgs().getFirst());
            } catch (NumberFormatException ignored) {
                // If the first argument is not a number, use the default count
            }
        }
        return 1;
    }

    private boolean isNumeric(String str) {
        return str.matches("\\d+");
    }

    @Override
    public Command getSupportedCommand() {
        return Command.REDDIT;
    }
}
