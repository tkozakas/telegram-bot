package org.churk.telegrambot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum SubCommand {
    REGISTER("register"),
    STATS("stats"),
    ALL("all"),
    YEAR("year"),
    USER("user");

    private final String command;

    public static SubCommand getSubCommand(String lowerCase) {
        return Arrays.stream(SubCommand.values())
                .filter(subCommand -> subCommand.getCommand().equals(lowerCase))
                .findFirst()
                .orElse(ALL);
    }
}
