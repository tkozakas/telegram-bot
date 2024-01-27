package org.churk.telegrambot.handler;

import lombok.AllArgsConstructor;
import org.churk.telegrambot.builder.MessageBuilderFactory;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.service.InstagramService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@AllArgsConstructor
public class InstagramHandler implements CommandHandler {
    private final Pattern pattern = Pattern.compile("https://www\\.instagram\\.com/(?:p|reel|tv)/([^/?]+)/");
    private final MessageBuilderFactory messageBuilderFactory;
    private final InstagramService instagramService;

    @Override
    public List<Validable> handle(HandlerContext context) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();

        String postCode = context.getArgs().size() == 2 ? context.getArgs().get(1) : "";
        Matcher matcher = pattern.matcher(postCode);
        if (!matcher.find() || postCode.isBlank()) {
            return List.of(messageBuilderFactory
                    .createTextMessageBuilder(chatId)
                    .withText("Please provide a valid url using /reels <url>")
                    .withReplyToMessageId(messageId)
                    .build());
        }
        String identifier = matcher.group(1);
        Optional<File> file = instagramService.getInstagramMedia(identifier);
        if (file.isPresent()) {
            File existingFile = file.get();
            return List.of(messageBuilderFactory
                    .createVideoMessage(chatId)
                    .withVideo(existingFile)
                    .build());
        }
        return List.of();
    }

    @Override
    public Command getSupportedCommand() {
        return Command.REELS;
    }
}
