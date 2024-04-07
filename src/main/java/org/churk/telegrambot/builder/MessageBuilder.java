package org.churk.telegrambot.builder;

import org.churk.telegrambot.model.MessageParams;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.util.List;
import java.util.Map;

@FunctionalInterface
public interface MessageBuilder {
    List<Validable> build(Map<MessageParams, Object> params);
}
