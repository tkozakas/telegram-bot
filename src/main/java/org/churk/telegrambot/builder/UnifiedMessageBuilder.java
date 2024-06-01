package org.churk.telegrambot.builder;

import org.churk.telegrambot.model.MessageParams;
import org.churk.telegrambot.model.MessageType;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.interfaces.Validable;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UnifiedMessageBuilder {

    private void setField(Object target, String methodName, Object value) {
        try {
            Method method = target.getClass().getMethod(methodName, value.getClass());
            method.invoke(target, value);
        } catch (Exception e) {
            throw new IllegalStateException("Error setting field: " + methodName, e);
        }
    }

    private SendMessage createSendMessage(Map<MessageParams, Object> params) {
        SendMessage sendMessage = new SendMessage();
        params.forEach((key, value) -> {
            switch (key) {
                case CHAT_ID -> setField(sendMessage, "setChatId", String.valueOf(value));
                case TEXT -> setField(sendMessage, "setText", value);
                case REPLY_TO_MESSAGE_ID -> setField(sendMessage, "setReplyToMessageId", value);
                case MARKDOWN -> setField(sendMessage, "setParseMode", "Markdown");
                default -> throw new IllegalStateException("Unexpected value: " + key);
            }
        });
        return sendMessage;
    }

    private SendPhoto createSendPhoto(Map<MessageParams, Object> params) {
        SendPhoto sendPhoto = new SendPhoto();
        params.forEach((key, value) -> {
            switch (key) {
                case CHAT_ID -> setField(sendPhoto, "setChatId", String.valueOf(value));
                case PHOTO -> setField(sendPhoto, "setPhoto", new InputFile((File) value));
                case CAPTION -> setField(sendPhoto, "setCaption", value);
                case REPLY_TO_MESSAGE_ID -> setField(sendPhoto, "setReplyToMessageId", value);
                default -> throw new IllegalStateException("Unexpected value: " + key);
            }
        });
        return sendPhoto;
    }

    private SendAnimation createSendAnimation(Map<MessageParams, Object> params) {
        SendAnimation sendAnimation = new SendAnimation();
        params.forEach((key, value) -> {
            switch (key) {
                case CHAT_ID -> setField(sendAnimation, "setChatId", String.valueOf(value));
                case ANIMATION -> setField(sendAnimation, "setAnimation", new InputFile((File) value));
                case CAPTION -> setField(sendAnimation, "setCaption", value);
                case REPLY_TO_MESSAGE_ID -> setField(sendAnimation, "setReplyToMessageId", value);
                default -> throw new IllegalStateException("Unexpected value: " + key);
            }
        });
        return sendAnimation;
    }

    private SendSticker createSendSticker(Map<MessageParams, Object> params) {
        SendSticker sendSticker = new SendSticker();
        params.forEach((key, value) -> {
            switch (key) {
                case CHAT_ID -> setField(sendSticker, "setChatId", String.valueOf(value));
                case STICKER -> setField(sendSticker, "setSticker", new InputFile((String) value));
                case REPLY_TO_MESSAGE_ID -> setField(sendSticker, "setReplyToMessageId", value);
                default -> throw new IllegalStateException("Unexpected value: " + key);
            }
        });
        return sendSticker;
    }

    private SendVideo createSendVideo(Map<MessageParams, Object> params) {
        SendVideo sendVideo = new SendVideo();
        params.forEach((key, value) -> {
            switch (key) {
                case CHAT_ID -> setField(sendVideo, "setChatId", String.valueOf(value));
                case VIDEO -> setField(sendVideo, "setVideo", new InputFile((File) value));
                case CAPTION -> setField(sendVideo, "setCaption", value);
                case REPLY_TO_MESSAGE_ID -> setField(sendVideo, "setReplyToMessageId", value);
                default -> throw new IllegalStateException("Unexpected value: " + key);
            }
        });
        return sendVideo;
    }

    private SendMediaGroup createSendMediaGroup(Map<MessageParams, Object> params) {
        SendMediaGroup sendMediaGroup = new SendMediaGroup();
        params.forEach((key, value) -> {
            switch (key) {
                case CHAT_ID -> setField(sendMediaGroup, "setChatId", String.valueOf(value));
                case MEDIA_GROUP -> {
                    List<Map.Entry<String, File>> captionFilePairs = (List<Map.Entry<String, File>>) value;
                    List<InputMedia> medias = captionFilePairs.stream()
                            .map(pair -> {
                                String caption = pair.getKey();
                                File file = pair.getValue();
                                String mediaName = UUID.randomUUID().toString();
                                return (InputMedia) InputMediaPhoto.builder()
                                        .media("attach://" + mediaName)
                                        .mediaName(mediaName)
                                        .caption(caption)
                                        .isNewMedia(true)
                                        .newMediaFile(file)
                                        .parseMode(ParseMode.HTML)
                                        .build();
                            }).collect(Collectors.toList());
                    setField(sendMediaGroup, "setMedias", medias);
                }
                case MESSAGE_ID -> setField(sendMediaGroup, "setReplyToMessageId", value);
                default -> throw new IllegalStateException("Unexpected value: " + key);
            }
        });
        return sendMediaGroup;
    }

    public List<Validable> build(MessageType messageType, Map<MessageParams, Object> params) {
        return switch (messageType) {
            case TEXT -> List.of(createSendMessage(params));
            case PHOTO -> List.of(createSendPhoto(params));
            case ANIMATION -> List.of(createSendAnimation(params));
            case STICKER -> List.of(createSendSticker(params));
            case VIDEO -> List.of(createSendVideo(params));
            case MEDIA_GROUP -> List.of(createSendMediaGroup(params));
        };
    }
}