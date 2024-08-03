package org.churk.telegrambot.model;

import lombok.*;
import org.churk.telegrambot.handler.HandlerFactory;

import java.util.List;
import java.util.Objects;

@Data
@Builder
public class UpdateContext {
    private HandlerFactory handlerFactory;
    private Command command;
    private List<String> args;
    private Long chatId;
    private Long userId;
    private Integer messageId;
    private String firstName;
    private boolean isReply;
}
