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

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class UnifiedMessageBuilder {
    private SendMessage createSendMessage(Map<MessageParams, Object> params) {
        SendMessage sendMessage = new SendMessage();
        params.forEach((key, value) -> {
            switch (key) {
                case CHAT_ID -> sendMessage.setChatId(String.valueOf(value));
                case TEXT -> sendMessage.setText((String) value);
                case REPLY_TO_MESSAGE_ID -> sendMessage.setReplyToMessageId((Integer) value);
                case MARKDOWN -> sendMessage.setParseMode(ParseMode.MARKDOWN);
            }
        });
        return sendMessage;
    }

    private SendPhoto createSendPhoto(Map<MessageParams, Object> params) {
        SendPhoto sendPhoto = new SendPhoto();
        params.forEach((key, value) -> {
            switch (key) {
                case CHAT_ID -> sendPhoto.setChatId(String.valueOf(value));
                case PHOTO -> sendPhoto.setPhoto(new InputFile((String) value));
                case CAPTION -> sendPhoto.setCaption((String) value);
                case REPLY_TO_MESSAGE_ID -> sendPhoto.setReplyToMessageId((Integer) value);
            }
        });
        return sendPhoto;
    }

    private SendAnimation createSendAnimation(Map<MessageParams, Object> params) {
        SendAnimation sendAnimation = new SendAnimation();
        params.forEach((key, value) -> {
            switch (key) {
                case CHAT_ID -> sendAnimation.setChatId(String.valueOf(value));
                case ANIMATION -> sendAnimation.setAnimation(new InputFile((String) value));
                case CAPTION -> sendAnimation.setCaption((String) value);
                case REPLY_TO_MESSAGE_ID -> sendAnimation.setReplyToMessageId((Integer) value);
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
                case STICKER -> sendSticker.setSticker(new InputFile((String) value));
            }
        });
        return sendSticker;
    }

    private SendVideo createSendVideo(Map<MessageParams, Object> params) {
        SendVideo sendVideo = new SendVideo();
        params.forEach((key, value) -> {
            switch (key) {
                case CHAT_ID -> sendVideo.setChatId(String.valueOf(value));
                case VIDEO -> sendVideo.setVideo(new InputFile((String) value));
                case CAPTION -> sendVideo.setCaption((String) value);
                case REPLY_TO_MESSAGE_ID -> sendVideo.setReplyToMessageId((Integer) value);
            }
        });
        return sendVideo;
    }

    private SendMediaGroup createSendMediaGroup(Map<MessageParams, Object> params) {
        SendMediaGroup sendMediaGroup = new SendMediaGroup();
        params.forEach((key, value) -> {
            switch (key) {
                case CHAT_ID -> sendMediaGroup.setChatId(String.valueOf(value));
                case MEDIA_GROUP -> sendMediaGroup.setMedias((List<InputMedia>) value);
                case MESSAGE_ID -> sendMediaGroup.setReplyToMessageId((Integer) value);
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
                    byte[] audioBytes = (byte[]) value;
                    sendAudio.setAudio(new InputFile(new ByteArrayInputStream(audioBytes), "audio.mp3"));
                }
                case CAPTION -> sendAudio.setCaption((String) value);
                case REPLY_TO_MESSAGE_ID -> sendAudio.setReplyToMessageId((Integer) value);
                case MARKDOWN -> sendAudio.setParseMode(ParseMode.MARKDOWN);
            }
        });
        return sendAudio;
    }

    private SendDocument createSendDocument(Map<MessageParams, Object> params) {
        SendDocument sendDocument = new SendDocument();
        params.forEach((key, value) -> {
            switch (key) {
                case CHAT_ID -> sendDocument.setChatId(String.valueOf(value));
                case DOCUMENT -> sendDocument.setDocument(new InputFile((String) value));
                case CAPTION -> sendDocument.setCaption((String) value);
                case REPLY_TO_MESSAGE_ID -> sendDocument.setReplyToMessageId((Integer) value);
            }
        });
        return sendDocument;
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
            case DOCUMENT -> List.of(createSendDocument(params));
        };
    }
}
