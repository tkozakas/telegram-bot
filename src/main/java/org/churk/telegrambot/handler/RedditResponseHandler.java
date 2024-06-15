package org.churk.telegrambot.handler;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrambot.builder.ListResponseHandler;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.model.RedditPost;
import org.churk.telegrambot.model.SubCommand;
import org.churk.telegrambot.model.Subreddit;
import org.churk.telegrambot.service.SubredditService;
import org.churk.telegrambot.utility.UpdateContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaVideo;

import java.io.File;
import java.util.AbstractMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedditResponseHandler extends ListResponseHandler<Subreddit> {
    private static final String REDDIT_URL = "https://www.reddit.com/r/";
    private final SubredditService subredditService;

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
                        yield handlePost(context, firstArg, count);
                    }
                }
                yield handlePost(context, firstArg, 1);
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
        return handlePost(context, subreddit, count);
    }

    private List<Validable> handleAdd(UpdateContext context) {
        String args = context.getArgs().getLast();
        Long chatId = context.getUpdate().getMessage().getChatId();

        if (context.getArgs().size() != 2 || !subredditService.isValidSubreddit(args)) {
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

        if (context.getArgs().size() != 2 || !subredditService.isValidSubreddit(args)) {
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

    private List<Validable> handlePost(UpdateContext context, String subreddit, int count) {
        return getRedditPosts(subreddit, context, count);
    }

    private List<Validable> getRedditPosts(String subreddit, UpdateContext context, int count) {
        try {
            if (!subredditService.isValidSubreddit(subreddit)) {
                return createReplyMessage(context,
                        "Please provide a valid name %s %s <subreddit>"
                                .formatted(Command.REDDIT.getPatternCleaned(botProperties.getWinnerName()), SubCommand.REMOVE.getCommand().getFirst()));
            }
            List<RedditPost> posts = subredditService.getRedditPosts(subreddit, count);
            if (posts.isEmpty()) {
                return createReplyMessage(context, "No posts available in r/%s".formatted(subreddit));
            }
            return fetchAndProcessMemes(context, posts, subreddit);
        } catch (FeignException.BadGateway |
                 FeignException.InternalServerError |
                 FeignException.GatewayTimeout e) {
            log.error("Reddit api is dead", e);
            return createLogMessage(context,
                    "The api is dead :)",
                    e.getMessage()
            );
        } catch (FeignException.ServiceUnavailable |
                 FeignException.NotFound |
                 FeignException.UnprocessableEntity e) {
            log.error("Subreddit does not exist", e);
            return createLogMessage(context,
                    "Subreddit does not exist",
                    e.getMessage());
        } catch (FeignException e) {
            log.error("An error occurred", e);
            return createLogMessage(context,
                    "An error occurred",
                    e.getMessage());
        }
    }

    private List<Validable> fetchAndProcessMemes(UpdateContext context, List<RedditPost> posts, String subreddit) {
        List<AbstractMap.SimpleEntry<String, File>> files = posts.stream()
                .map(post -> subredditService.getFile(post)
                        .map(file -> new AbstractMap.SimpleEntry<>(formatCaption(post, subreddit), file)))
                .flatMap(Optional::stream)
                .toList();

        if (files.isEmpty()) {
            return posts.stream()
                    .map(post -> postWithoutFileResponse(context, post, subreddit))
                    .flatMap(List::stream)
                    .toList();
        }

        if (files.size() == 1) {
            File file = files.getFirst().getValue();
            return postWithFileResponse(context, posts.getFirst(), file, subreddit);
        } else {
            convertGifsToMp4(files);
            List<InputMedia> medias = createInputMediaList(files);
            return createMediaGroupMessage(context, medias);
        }
    }

    private String formatCaption(RedditPost post, String subreddit) {
        return "%s%nFrom r/%s".formatted(post.getTitle() != null ? post.getTitle() : "", subreddit);
    }

    private void convertGifsToMp4(List<AbstractMap.SimpleEntry<String, File>> files) {
        files.forEach(entry -> {
            File file = entry.getValue();
            if (file.getName().toLowerCase().endsWith(".gif")) {
                Optional<File> mp4File = subredditService.convertGifToMp4(file);
                if (mp4File.isEmpty()) {
                    return;
                }
                entry.setValue(mp4File.get());
            }
        });
    }

    private List<InputMedia> createInputMediaList(List<AbstractMap.SimpleEntry<String, File>> files) {
        return files.stream()
                .map(pair -> {
                    String caption = pair.getKey();
                    File file = pair.getValue();
                    String mediaName = file.getPath();
                    return createInputMedia(file, mediaName, caption);
                })
                .collect(Collectors.toList());
    }

    private InputMedia createInputMedia(File file, String mediaName, String caption) {
        if (isVideoFile(file)) {
            return InputMediaVideo.builder()
                    .media("attach://" + mediaName)
                    .mediaName(mediaName)
                    .caption(caption)
                    .isNewMedia(true)
                    .newMediaFile(file)
                    .parseMode(ParseMode.HTML)
                    .build();
        } else {
            return InputMediaPhoto.builder()
                    .media("attach://" + mediaName)
                    .mediaName(mediaName)
                    .caption(caption)
                    .isNewMedia(true)
                    .newMediaFile(file)
                    .parseMode(ParseMode.HTML)
                    .build();
        }
    }

    private boolean isVideoFile(File file) {
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".mp4") || fileName.endsWith(".mov") || fileName.endsWith(".wmv") || fileName.endsWith(".avi");
    }


    private String chooseSubreddit(Long chatId) {
        List<Subreddit> subreddits = subredditService.getSubreddits(chatId);
        return subreddits.isEmpty() ? null :
                subreddits.get(ThreadLocalRandom.current().nextInt(subreddits.size())).getSubredditName();
    }

    private List<Validable> postWithoutFileResponse(UpdateContext context, RedditPost post, String subreddit) {
        String caption = "%s%n<Image unavailable>%nFrom r/%s".formatted(post.getTitle() != null ? post.getTitle() : "", subreddit);
        return createTextMessage(context, caption);
    }

    private List<Validable> postWithFileResponse(UpdateContext context, RedditPost post, File file, String subreddit) {
        String caption = "%s%nFrom r/%s".formatted(post.getTitle() != null ? post.getTitle() : "", subreddit);
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".gif") ?
                createAnimationMessage(context, file, caption) :
                createPhotoMessage(context, file, caption);
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
