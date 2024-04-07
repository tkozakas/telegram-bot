package org.churk.telegrambot.builder;

import org.churk.telegrambot.model.MessageType;
import org.springframework.stereotype.Service;

@Service
public class MessageBuilderFactory {
    public MessageBuilder getBuilder(MessageType messageType) {
        if (messageType == MessageType.TEXT) {
            return new TextMessageBuilder();
        } else if (messageType == MessageType.PHOTO) {
            return new PhotoMessageBuilder();
        } else if (messageType == MessageType.ANIMATION) {
            return new AnimationMessageBuilder();
        } else if (messageType == MessageType.STICKER) {
            return new StickerMessageBuilder();
        } else if (messageType == MessageType.VIDEO) {
            return new VideoMessageBuilder();
        }
        throw new IllegalArgumentException("Unsupported message type: " + messageType);
    }
}
