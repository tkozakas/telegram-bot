package org.churk.telegrambot.handler;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrambot.builder.ListResponseHandler;
import org.churk.telegrambot.client.MemeClient;
import org.churk.telegrambot.model.*;
import org.churk.telegrambot.service.SubredditService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedditResponseHandler extends ListResponseHandler<Subreddit> {
    private static final String REDDIT_URL = "https://www.reddit.com/r/";
    private final SubredditService subredditService;
    private final MemeClient memeClient;

    @Override
    public List<Validable> handle(UpdateContext context) {
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
                if (args.size() == 1) {
                    if (isNumeric(firstArg)) {
                        int count = Integer.parseInt(firstArg);
                        yield handleRandomPost(context, count);
                    }
                } else {
                    String secondArg = context.getArgs().get(1);
                    if (isNumeric(secondArg)) {
                        int count = Integer.parseInt(secondArg);
                        yield getRedditPosts(firstArg, context, count);
                    }
                }
                yield getRedditPosts(firstArg, context, 1);
            }
            default -> throw new IllegalStateException("Unexpected value: " + subCommand);
        };
    }

    private List<Validable> handleRandomPost(UpdateContext context, int count) {
        Long chatId = context.getUpdate().getMessage().getChatId();

        if (subredditService.getSubreddits(chatId).isEmpty()) {
            return createReplyMessage(context,
                    "No subreddits available. Use %s <%s>"
                            .formatted(Command.REDDIT.getPatternCleaned(), SubCommand.ADD.getCommand().getFirst()));
        }
        String subreddit = chooseSubreddit(chatId);
        return getRedditPosts(subreddit, context, count);
    }

    private List<Validable> handleAdd(UpdateContext context) {
        String args = context.getArgs().getLast();
        Long chatId = context.getUpdate().getMessage().getChatId();

        if (context.getArgs().size() != 2) {
            return createReplyMessage(context,
                    "Please provide a valid name %s %s <subreddit>"
                            .formatted(Command.REDDIT.getPatternCleaned(botProperties.getWinnerName()), SubCommand.ADD.getCommand().getFirst()));
        }
        String subreddit = args.startsWith(REDDIT_URL) ? args.replace(REDDIT_URL, "") : args;
        if (subredditService.existsByChatIdAndSubredditName(chatId, subreddit)) {
            return createReplyMessage(context, "Subreddit %s already exists in the list".formatted(subreddit));
        }
        subredditService.addSubreddit(chatId, subreddit);
        return createReplyMessage(context, "Subreddit %s added".formatted(subreddit));
    }

    private List<Validable> handleList(UpdateContext context) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        List<Subreddit> subreddits = subredditService.getSubreddits(chatId);
        Function<Subreddit, String> subredditFormatter = subreddit -> String.format("- r/*%s*\n", subreddit.getSubredditName());
        context.setMarkdown(true);
        return formatListResponse(context, subreddits, subredditFormatter,
                "Subreddits:\n",
                "",
                "No subreddits available");
    }

    private List<Validable> handleRemove(UpdateContext context) {
        String args = context.getArgs().getLast();
        Long chatId = context.getUpdate().getMessage().getChatId();

        if (context.getArgs().size() != 2) {
            return createReplyMessage(context,
                    "Please provide a valid name %s %s <subreddit>"
                            .formatted(Command.REDDIT.getPatternCleaned(botProperties.getWinnerName()), SubCommand.REMOVE.getCommand().getFirst()));
        }
        if (!subredditService.existsByChatIdAndSubredditName(chatId, args)) {
            return createReplyMessage(context, "Subreddit %s does not exist in the list".formatted(args));
        }
        subredditService.deleteSubreddit(chatId, args);
        return createReplyMessage(context, "Subreddit %s removed".formatted(args));
    }

    private List<Validable> getRedditPosts(String subreddit, UpdateContext context, int count) {
        try {
            List<RedditPost> posts = memeClient.getRedditPost(subreddit, count).getBody();
            if (posts.isEmpty()) {
                return createReplyMessage(context, "No posts available in r/%s".formatted(subreddit));
            }
            Set<String> seenUrls = new HashSet<>();
            List<Validable> mediaList = posts.stream()
                    .filter(post -> seenUrls.add(post.getUrl()))
                    .map(post -> {
                        String title = "From r/%s\n%s".formatted(subreddit, post.getTitle());
                        String url = post.getUrl();
                        if (post.isVideo()) {
                            return createVideoMessage(context, url, title);
                        }
                        if (post.isImage()) {
                            return createPhotoMessage(context, url, title);
                        }
                        if (post.isGif()) {
                            return createAnimationMessage(context, url, title);
                        }
                        return createTextMessage(context, title);
                    })
                    .flatMap(List::stream)
                    .toList();
            if (mediaList.size() == 1) {
                return mediaList;
            }
            return createMediaGroupMessage(context, mediaList);
        } catch (FeignException e) {
            log.error("Error while fetching posts from Reddit", e);
            return createReplyMessage(context, "Error while fetching posts from Reddit");
        }
    }

    private String chooseSubreddit(Long chatId) {
        List<Subreddit> subreddits = subredditService.getSubreddits(chatId);
        return subreddits.isEmpty() ? null :
                subreddits.get(ThreadLocalRandom.current().nextInt(subreddits.size())).getSubredditName();
    }

    private int parseCount(UpdateContext context) {
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
