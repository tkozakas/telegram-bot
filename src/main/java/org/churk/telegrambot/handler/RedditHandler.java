package org.churk.telegrambot.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrambot.builder.MessageBuilderFactory;
import org.churk.telegrambot.config.BotProperties;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.service.RedditService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
@AllArgsConstructor
public class RedditHandler implements CommandHandler {
    private final BotProperties botProperties;
    private final MessageBuilderFactory messageBuilderFactory;
    private final RedditService redditService;

    @Override
    public List<Validable> handle(HandlerContext context) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();
        List<String> arguments = context.getArgs();
        return getRedditFile(arguments, chatId, messageId);
    }

    private List<Validable> getRedditFile(List<String> arguments, Long chatId, Integer messageId) {
        String subreddit = (!arguments.isEmpty()) ?
                arguments.getFirst() :
                botProperties.getSubredditNames().get(ThreadLocalRandom.current().nextInt(botProperties.getSubredditNames().size()));
        try {
            Optional<File> file = redditService.getMemeFromSubreddit(subreddit);
            if (file.isPresent()) {
                File existingFile = file.get();
                existingFile.deleteOnExit();
                String fileName = file.get().getName().toLowerCase();
                return fileName.endsWith(".gif") ?
                        getAnimationMessage(chatId, existingFile, subreddit) :
                        getPhotoMessage(chatId, existingFile, subreddit);
            }
        } catch (Exception e) {
            return getErrorMessage(chatId, messageId, "Something went wrong, please try again later");
        }
        return List.of();
    }

    private List<Validable> getPhotoMessage(Long chatId, File file, String subreddit) {
        log.info("Sending photo message to chatId: {}", chatId);
        return List.of(messageBuilderFactory
                .createPhotoMessageBuilder(chatId)
                .withPhoto(file)
                .withCaption("From r/" + subreddit)
                .build());
    }

    private List<Validable> getAnimationMessage(Long chatId, File file, String subreddit) {
        log.info("Sending animation message to chatId: {}", chatId);
        return List.of(messageBuilderFactory
                .createAnimationMessageBuilder(chatId)
                .withAnimation(file)
                .withCaption("From r/" + subreddit)
                .build());
    }

    private List<Validable> getErrorMessage(Long chatId, Integer messageId, String text) {
        return List.of(messageBuilderFactory
                .createTextMessageBuilder(chatId)
                .withReplyToMessageId(messageId)
                .withText(text)
                .build());
    }

    @Override
    public Command getSupportedCommand() {
        return Command.REDDIT;
    }
}
