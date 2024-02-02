package org.churk.telegrambot.instagram;

import lombok.RequiredArgsConstructor;
import org.churk.telegrambot.handler.Command;
import org.churk.telegrambot.handler.MessageCreationService;
import org.churk.telegrambot.utility.HandlerContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class InstagramMessageCreationService extends MessageCreationService {
    private final Pattern pattern = Pattern.compile("https://www\\.instagram\\.com/(?:p|reel|tv)/([^/?]+)/");
    private final InstagramService instagramService;

    @Override
    public List<Validable> handle(HandlerContext context) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();

        String postCode = context.getArgs().size() == 2 ? context.getArgs().get(1) : "";
        Matcher matcher = pattern.matcher(postCode);
        if (!matcher.find() || postCode.isBlank()) {
            return getReplyMessage(chatId, messageId,
                    "Please provide a valid link /reels <link>");
        }
        String identifier = matcher.group(1);
        Optional<File> file = instagramService.getInstagramMedia(identifier);
        if (file.isPresent()) {
            File existingFile = file.get();
            return getVideo(chatId, existingFile);
        }
        return List.of();
    }

    @Override
    public Command getSupportedCommand() {
        return Command.REELS;
    }
}
