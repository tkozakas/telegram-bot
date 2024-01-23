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
    DAILY_MESSAGE(".*/%s\\b.*"),
    FACT(".*/fact\\b.*"),
    STICKER(".*/sticker\\b.*"),
    MEME(".*/reddit\\b.*");

    private final String pattern;

    public static Command getCommand(String text, String botName) {
        return Arrays.stream(Command.values())
                .filter(command -> text.matches(command.getPattern().formatted(botName)))
                .findFirst()
                .orElse(null);
    }
}
