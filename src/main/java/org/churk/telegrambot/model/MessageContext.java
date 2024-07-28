package org.churk.telegrambot.model;

import lombok.Builder;
import lombok.Data;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

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
    private String gifUrl;
    private File document;
    private String photoUrl;
    private String videoUrl;
    private byte[] audioStream;
    private Sticker sticker;
    private List<Validable> mediaList;
    private String mediaUrl;
}
