package org.churk.telegrambot.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrambot.builder.MessageBuilderFactory;
import org.churk.telegrambot.service.RedditService;
import org.telegram.telegrambots.meta.api.interfaces.Validable;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
public class RedditHandler implements CommandHandler {
    private final MessageBuilderFactory messageBuilderFactory;
    private final RedditService redditService;
    private final List<String> arguments;
    @Override
    public List<Validable> handle(Update update) {
        Long chatId = update.getMessage().getChatId();
        String subreddit = (arguments.size() == 2) ? arguments.get(1) : null;
        try {
            Optional<File> file = subreddit == null ?
                    redditService.getMeme() :
                    redditService.getMemeFromSubreddit(subreddit);
            if (file.isPresent()) {
                File existingFile = file.get();
                String fileName = file.get().getName().toLowerCase();
                return fileName.endsWith(".gif") ?
                        getAnimationMessage(chatId, existingFile) :
                        getPhotoMessage(chatId, existingFile);
            }
        } catch (Exception e) {
            return getErrorMessage(e, chatId);
        }
        return List.of();
    }

    private List<Validable> getPhotoMessage(Long chatId, File file) {
        log.info("Sending photo message to chatId: {}", chatId);
        return List.of(messageBuilderFactory
                .createPhotoMessageBuilder(chatId)
                .withPhoto(file)
                .build());
    }

    private List<Validable> getAnimationMessage(Long chatId, File file) {
        log.info("Sending animation message to chatId: {}", chatId);
        return List.of(messageBuilderFactory
                .createAnimationMessageBuilder(chatId)
                .withAnimation(file)
                .build());
    }

    private List<Validable> getErrorMessage(Exception e, Long chatId) {
        log.error("Error occurred while fetching meme: {}", e.getMessage());
        return List.of(messageBuilderFactory
                .createTextMessageBuilder(chatId)
                .withText("An error occurred while fetching the meme.")
                .build());
    }
}
