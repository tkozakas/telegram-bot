package org.churk.telegrambot.handler;

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
    MARKDOWN;
}
