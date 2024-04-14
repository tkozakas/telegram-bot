package org.churk.telegrambot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum SubCommand {
    REGISTER(List.of("register", "reg")),
    STATS(List.of("stats")),
    ALL(List.of("all")),
    YEAR(List.of("year")),
    USER(List.of("user")),
    ADD(List.of("add")),
    LIST(List.of("list")),
    REMOVE(List.of("remove")),
    RANDOM(List.of("random")),
    NONE(List.of(""));

    private final List<String> command;

    public static SubCommand getSubCommand(String lowerCase) {
        return Arrays.stream(values())
                .filter(subCommand -> subCommand.getCommand().contains(lowerCase))
                .findFirst()
                .orElse(NONE);
    }
}
