package org.churk.telegrambot.builder;

import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;


public class VideoMessageBuilder {
    private final SendVideo sendVideo;

    public VideoMessageBuilder(Long chatId) {
        this.sendVideo = new SendVideo();
        this.sendVideo.setChatId(String.valueOf(chatId));
    }

    public VideoMessageBuilder withVideo(File video) {
        sendVideo.setVideo(new InputFile(video));
        return this;
    }

    public VideoMessageBuilder withCaption(String caption) {
        sendVideo.setCaption(caption);
        return this;
    }

    public VideoMessageBuilder withReplyToMessageId(Integer messageId) {
        sendVideo.setReplyToMessageId(messageId);
        return this;
    }

    public SendVideo build() {
        return sendVideo;
    }
}
