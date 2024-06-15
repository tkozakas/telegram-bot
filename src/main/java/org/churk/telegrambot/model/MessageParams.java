package org.churk.telegrambot.model;

import lombok.Getter;

@Getter
public enum MessageParams {
    CHAT_ID,
    MESSAGE_ID,
    TEXT,
    CAPTION,
    STICKER,
    PHOTO,
    ANIMATION,
    VIDEO,
    REPLY_TO_MESSAGE_ID,
    MEDIA_GROUP,
    AUDIO,
    DOCUMENT,
    MARKDOWN
}
