package org.churk.telegrambot.model;

import lombok.Builder;
import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;

import java.io.File;
import java.util.List;

@Data
@Builder
public class MessageContext {
    private Long chatId;
    private String caption;
    private String text;
    private boolean isReply;
    private Integer replyToMessageId;
    private boolean isMarkdown;
    private File animation;
    private File document;
    private File photo;
    private File video;
    private File audio;
    private Sticker sticker;
    private List<InputMedia> mediaGroup;
}
