package org.churk.telegrambot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum Command {
    START(".*/start\\b.*", "Start the bot"),
    HELP(".*/help\\b.*", "Get help"),
    REGISTER(".*/%sreg\\b.*", "Register yourself as a %s"),
    DAILY_MESSAGE(".*/%s\\b.*", "Get today's %s"),
    STATS(".*/%sstats\\b.*", "Get stats (use %sstats [year] for specific year)"),
    STATS_ALL(".*/%sall\\b.*", "Get all-time stats"),
    STATS_USER(".*/%sme\\b.*", "Get personal stats"),
    FACT(".*/fact\\b.*", "Random fact of the day"),
    STICKER(".*/sticker\\b.*", "Random sticker from a %s sticker set"),
    REDDIT(".*/reddit\\b.*", "Random reddit picture (use /reddit [year] for specific subreddit)");

    private final String pattern;
    private final String description;

    public static Command getTextCommand(String text, String botName) {
        return Arrays.stream(Command.values())
                .filter(command -> text.matches(command.getPattern().formatted(botName)))
                .findFirst()
                .orElse(null);
    }
}
