package org.churk.telegrambot.builder;

import lombok.extern.slf4j.Slf4j;
import org.churk.telegrambot.model.MessageParams;
import org.churk.telegrambot.model.MessageType;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.interfaces.Validable;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class UnifiedMessageBuilder {
    public static List<File> files = new ArrayList<>();

    public static void clearFiles() {
        UnifiedMessageBuilder.files.forEach(UnifiedMessageBuilder::deleteFile);
        UnifiedMessageBuilder.files = new ArrayList<>();
    }

    private static void deleteFile(File file) {
        try {
            String path = file.getPath();
            if (path.contains("attach:")) {
                path = path.replace("attach:", "");
            }
            Files.deleteIfExists(Path.of(path));
        } catch (IOException e) {
            log.error("Error while deleting file: {}", file.getAbsolutePath(), e);
            throw new RuntimeException(e);
        }
    }

    private SendMessage createSendMessage(Map<MessageParams, Object> params) {
        SendMessage sendMessage = new SendMessage();
        params.forEach((key, value) -> {
            switch (key) {
                case CHAT_ID -> sendMessage.setChatId(String.valueOf(value));
                case TEXT -> sendMessage.setText((String) value);
                case REPLY_TO_MESSAGE_ID -> sendMessage.setReplyToMessageId((Integer) value);
                case MARKDOWN -> sendMessage.setParseMode(ParseMode.MARKDOWN);
                default -> throw new IllegalStateException("Unexpected value: " + key);
            }
        });
        return sendMessage;
    }

    private SendPhoto createSendPhoto(Map<MessageParams, Object> params) {
        SendPhoto sendPhoto = new SendPhoto();
        params.forEach((key, value) -> {
            switch (key) {
                case CHAT_ID -> sendPhoto.setChatId(String.valueOf(value));
                case PHOTO -> {
                    File photoFile = (File) value;
                    sendPhoto.setPhoto(new InputFile(photoFile));
                    files.add(photoFile);
                }
                case CAPTION -> sendPhoto.setCaption((String) value);
                case REPLY_TO_MESSAGE_ID -> sendPhoto.setReplyToMessageId((Integer) value);
                default -> throw new IllegalStateException("Unexpected value: " + key);
            }
        });
        return sendPhoto;
    }

    private SendAnimation createSendAnimation(Map<MessageParams, Object> params) {
        SendAnimation sendAnimation = new SendAnimation();
        params.forEach((key, value) -> {
            switch (key) {
                case CHAT_ID -> sendAnimation.setChatId(String.valueOf(value));
                case ANIMATION -> {
                    File animationFile = (File) value;
                    sendAnimation.setAnimation(new InputFile(animationFile));
                    files.add(animationFile);
                }
                case CAPTION -> sendAnimation.setCaption((String) value);
                case REPLY_TO_MESSAGE_ID -> sendAnimation.setReplyToMessageId((Integer) value);
                default -> throw new IllegalStateException("Unexpected value: " + key);
            }
        });
        return sendAnimation;
    }

    private SendSticker createSendSticker(Map<MessageParams, Object> params) {
        SendSticker sendSticker = new SendSticker();
        params.forEach((key, value) -> {
            switch (key) {
                case CHAT_ID -> sendSticker.setChatId(String.valueOf(value));
                case REPLY_TO_MESSAGE_ID -> sendSticker.setReplyToMessageId((Integer) value);
                case STICKER -> {
                    String stickerFilePath = (String) value;
                    sendSticker.setSticker(new InputFile(stickerFilePath));
                    files.add(new File(stickerFilePath));
                }
                default -> throw new IllegalStateException("Unexpected value: " + key);
            }
        });
        return sendSticker;
    }

    private SendVideo createSendVideo(Map<MessageParams, Object> params) {
        SendVideo sendVideo = new SendVideo();
        params.forEach((key, value) -> {
            switch (key) {
                case CHAT_ID -> sendVideo.setChatId(String.valueOf(value));
                case VIDEO -> {
                    File videoFile = (File) value;
                    sendVideo.setVideo(new InputFile(videoFile));
                    files.add(videoFile);
                }
                case CAPTION -> sendVideo.setCaption((String) value);
                case REPLY_TO_MESSAGE_ID -> sendVideo.setReplyToMessageId((Integer) value);
                default -> throw new IllegalStateException("Unexpected value: " + key);
            }
        });
        return sendVideo;
    }

    private SendMediaGroup createSendMediaGroup(Map<MessageParams, Object> params) {
        SendMediaGroup sendMediaGroup = new SendMediaGroup();
        params.forEach((key, value) -> {
            switch (key) {
                case CHAT_ID -> sendMediaGroup.setChatId(String.valueOf(value));
                case MEDIA_GROUP -> {
                    List<InputMedia> mediaGroup = (List<InputMedia>) value;
                    sendMediaGroup.setMedias(mediaGroup);
                    mediaGroup.forEach(media -> files.add(new File(media.getMedia())));
                }
                case MESSAGE_ID -> sendMediaGroup.setReplyToMessageId((Integer) value);
                default -> throw new IllegalStateException("Unexpected value: " + key);
            }
        });
        return sendMediaGroup;
    }

    private SendAudio createSendAudio(Map<MessageParams, Object> params) {
        SendAudio sendAudio = new SendAudio();
        params.forEach((key, value) -> {
            switch (key) {
                case CHAT_ID -> sendAudio.setChatId(String.valueOf(value));
                case AUDIO -> {
                    File audioFile = (File) value;
                    sendAudio.setAudio(new InputFile(audioFile));
                    files.add(audioFile);
                }
                case CAPTION -> sendAudio.setCaption((String) value);
                case REPLY_TO_MESSAGE_ID -> sendAudio.setReplyToMessageId((Integer) value);
                default -> throw new IllegalStateException("Unexpected value: " + key);
            }
        });
        return sendAudio;
    }

    public List<Validable> build(MessageType messageType, Map<MessageParams, Object> params) {
        return switch (messageType) {
            case TEXT -> List.of(createSendMessage(params));
            case PHOTO -> List.of(createSendPhoto(params));
            case ANIMATION -> List.of(createSendAnimation(params));
            case STICKER -> List.of(createSendSticker(params));
            case VIDEO -> List.of(createSendVideo(params));
            case MEDIA_GROUP -> List.of(createSendMediaGroup(params));
            case AUDIO -> List.of(createSendAudio(params));
        };
    }
}
