package org.churk.telegrambot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum Command {
    REGISTER(".*/%sreg\\b.*"),
    STATS(".*/%sstats\\b.*"),
    STATS_ALL(".*/%sall\\b.*"),
    STATS_USER(".*/%sme\\b.*"),
    FACT(".*/fact\\b.*"),
    STICKER(".*/sticker\\b.*"),
    DAILY_MESSAGE(".*/%s\\b.*"),
    MEME(".*/reddit.*");

    private final String pattern;
    public static Command getCommand(String text) {
        return Arrays.stream(Command.values())
                .filter(command -> text.matches(command.getPattern()))
                .findFirst()
                .orElse(null);
    }
}
