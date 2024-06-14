package org.churk.telegrambot.handler;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrambot.builder.UnifiedMessageBuilder;
import org.churk.telegrambot.config.BotProperties;
import org.churk.telegrambot.model.MessageContext;
import org.churk.telegrambot.model.MessageParams;
import org.churk.telegrambot.model.MessageType;
import org.churk.telegrambot.model.Sticker;
import org.churk.telegrambot.service.DailyMessageService;
import org.churk.telegrambot.utility.UpdateContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.interfaces.Validable;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public abstract class Handler implements CommandHandler {
    @Autowired
    protected BotProperties botProperties;
    @Autowired
    protected DailyMessageService dailyMessageService;
    @Autowired
    protected UnifiedMessageBuilder unifiedMessageBuilder;

    protected List<Validable> createMessage(MessageType messageType, Map<MessageParams, Object> params) {
        Map<MessageParams, Object> filteredParams = params.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return unifiedMessageBuilder.build(messageType, filteredParams);
    }

    protected List<Validable> createMessage(MessageType messageType, MessageContext context) {
        Map<MessageParams, Object> params = new HashMap<>();
        params.put(MessageParams.CHAT_ID, context.getChatId());
        params.put(MessageParams.TEXT, context.getText());
        if (context.isReply()) {
            params.put(MessageParams.REPLY_TO_MESSAGE_ID, context.getReplyToMessageId());
        }
        if (context.isMarkdown()) {
            params.put(MessageParams.MARKDOWN, true);
        }
        params.put(MessageParams.DOCUMENT, context.getDocument());
        params.put(MessageParams.STICKER, context.getSticker() != null ? context.getSticker().getFileId() : null);
        params.put(MessageParams.ANIMATION, context.getAnimation());
        params.put(MessageParams.CAPTION, context.getCaption());
        params.put(MessageParams.PHOTO, context.getPhoto());
        params.put(MessageParams.VIDEO, context.getVideo());
        params.put(MessageParams.MEDIA_GROUP, context.getMediaGroup());
        params.put(MessageParams.AUDIO, context.getAudio());

        return createMessage(messageType, params);
    }

    protected List<Validable> createTextMessage(UpdateContext context, String text) {
        return createMessage(MessageType.TEXT, MessageContext.builder()
                .chatId(context.getUpdate().getMessage().getChatId())
                .text(text)
                .isMarkdown(context.isMarkdown())
                .isReply(context.isReply())
                .replyToMessageId(context.getUpdate().getMessage().getMessageId())
                .build());
    }

    protected List<Validable> createReplyMessage(UpdateContext context, String text) {
        return createMessage(MessageType.TEXT, MessageContext.builder()
                .chatId(context.getUpdate().getMessage().getChatId())
                .text(text)
                .isReply(true)
                .replyToMessageId(context.getUpdate().getMessage().getMessageId())
                .build());
    }

    protected List<Validable> createDocumentMessage(UpdateContext context, File file) {
        return createMessage(MessageType.DOCUMENT, MessageContext.builder()
                .chatId(context.getUpdate().getMessage().getChatId())
                .document(file)
                .build());
    }

    protected List<Validable> createStickerMessage(UpdateContext context, Sticker sticker) {
        return createMessage(MessageType.STICKER, MessageContext.builder()
                .chatId(context.getUpdate().getMessage().getChatId())
                .sticker(sticker)
                .replyToMessageId(context.getUpdate().getMessage().getMessageId())
                .build());
    }

    protected List<Validable> createAnimationMessage(UpdateContext context, File file, String caption) {
        return createMessage(MessageType.ANIMATION, MessageContext.builder()
                .chatId(context.getUpdate().getMessage().getChatId())
                .animation(file)
                .caption(caption)
                .build());
    }

    protected List<Validable> createPhotoMessage(UpdateContext context, File file, String caption) {
        return createMessage(MessageType.PHOTO, MessageContext.builder()
                .chatId(context.getUpdate().getMessage().getChatId())
                .photo(file)
                .caption(caption)
                .build());
    }

    protected List<Validable> createVideoMessage(UpdateContext context, File file, String caption) {
        return createMessage(MessageType.VIDEO, MessageContext.builder()
                .chatId(context.getUpdate().getMessage().getChatId())
                .video(file)
                .caption(caption)
                .build());
    }

    protected List<Validable> createMediaGroupMessage(UpdateContext context, List<InputMedia> mediaGroup) {
        return createMessage(MessageType.MEDIA_GROUP, MessageContext.builder()
                .chatId(context.getUpdate().getMessage().getChatId())
                .mediaGroup(mediaGroup)
                .build());
    }

    protected List<Validable> createAudioMessage(UpdateContext context, String caption, File audioFile) {
        return createMessage(MessageType.AUDIO, MessageContext.builder()
                .chatId(context.getUpdate().getMessage().getChatId())
                .replyToMessageId(context.getUpdate().getMessage().getMessageId())
                .caption(caption)
                .audio(audioFile)
                .build());
    }

    protected List<Validable> createLogMessage(UpdateContext context, String caption, String text) {
        File logFile = null;
        try {
            logFile = Files.createTempFile("log", ".txt").toFile();
            Files.writeString(logFile.toPath(), text + System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            log.error("Failed to write to log file: {}", logFile != null ? logFile.getPath() : "Unknown path", e);
        }
        return createMessage(MessageType.DOCUMENT, MessageContext.builder()
                .chatId(context.getUpdate().getMessage().getChatId())
                .replyToMessageId(context.getUpdate().getMessage().getMessageId())
                .isReply(true)
                .document(logFile)
                .caption(caption)
                .build());
    }


    protected String getAudioMessage(List<Validable> validables) {
        return validables.stream()
                .map(validable -> switch (validable) {
                    case SendMessage sendMessage -> sendMessage.getText();
                    case InputMedia inputMedia -> inputMedia.getCaption();
                    case SendAnimation sendAnimation -> sendAnimation.getCaption();
                    case SendPhoto sendPhoto -> sendPhoto.getCaption();
                    case SendVideo sendVideo -> sendVideo.getCaption();
                    default -> "";
                })
                .collect(Collectors.joining());
    }
}
