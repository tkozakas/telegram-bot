package org.churk.telegrambot.builder;

import org.churk.telegrambot.model.MessageParams;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.interfaces.Validable;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class MediaGroupMessageBuilder implements MessageBuilder {
    private final SendMediaGroup message = new SendMediaGroup();

    @Override
    public List<Validable> build(Map<MessageParams, Object> params) {
        params.forEach((key, value) -> {
            switch (key) {
                case MessageParams.CHAT_ID -> message.setChatId(String.valueOf(value));
                case MessageParams.MEDIA_GROUP -> {
                    List<?> files = (List<?>) value;
                    List<InputMedia> medias = files.stream()
                            .filter(File.class::isInstance)
                            .map(userContent -> {
                                String mediaName = UUID.randomUUID().toString();
                                return (InputMedia) InputMediaPhoto.builder()
                                        .media("attach://" + mediaName)
                                        .mediaName(mediaName)
                                        .isNewMedia(true)
                                        .newMediaFile((File) userContent)
                                        .parseMode(ParseMode.HTML)
                                        .build();
                            }).toList();
                    message.setMedias(medias);
                }
                case MessageParams.MESSAGE_ID -> message.setReplyToMessageId((Integer) value);
                default -> throw new IllegalStateException("Unexpected value: " + key);
            }
        });
        return List.of(message);
    }
}
