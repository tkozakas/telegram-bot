package org.churk.telegrambot.builder;

import org.telegram.telegrambots.meta.api.interfaces.Validable;

@FunctionalInterface
public interface MessageBuilderFunction<T> {
    Validable build(T builder);
}

