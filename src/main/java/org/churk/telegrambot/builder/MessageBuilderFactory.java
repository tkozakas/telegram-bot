package org.churk.telegrambot.builder;

import org.churk.telegrambot.model.MessageType;
import org.springframework.stereotype.Service;

@Service
public class MessageBuilderFactory {
    public MessageBuilder getBuilder(MessageType messageType) {
        return switch (messageType) {
            case TEXT -> new TextMessageBuilder();
            case PHOTO -> new PhotoMessageBuilder();
            case ANIMATION -> new AnimationMessageBuilder();
            case STICKER -> new StickerMessageBuilder();
            case VIDEO -> new VideoMessageBuilder();
            case MEDIA_GROUP -> new MediaGroupMessageBuilder();
        };
    }
}
