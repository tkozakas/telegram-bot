package org.churk.telegrambot.model.bot;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum Command {
    START(List.of(".*/start\\b.*"), "Start the bot"),
    HELP(List.of(".*/help\\b.*"), "Get help"),
    REGISTER(List.of(".*/%sreg\\b.*"), "Register yourself as a %s"),
    DAILY_MESSAGE(List.of(".*/%s\\b.*"), "Get today's %s"),
    STATS(List.of(".*/%sstats\\b.*"), "Get stats (use %sstats [year] for specific year)"),
    STATS_ALL(List.of(".*/%sall\\b.*"), "Get all-time stats"),
    STATS_USER(List.of(".*/%sme\\b.*"), "Get personal stats"),
    FACT(List.of(".*/fact\\b.*"), "Random fact of the day"),
    STICKER(List.of(".*/sticker\\b.*"), "Random sticker from a %s sticker set"),
    REDDIT(List.of(".*/reddit\\b.*", ".*/meme\\b.*"), "Random reddit picture (use /reddit [year] for specific subreddit)"),
    REELS(List.of(".*/reels\\b.*", ".*/reel\\b.*"), "Random reels video (use /reels [link] for specific reels video)");

    private final List<String> patterns;
    private final String description;

    public static Command getTextCommand(String text, String botName) {
        return Arrays.stream(Command.values())
                .filter(command -> command.getPatterns().stream()
                        .anyMatch(pattern -> text.matches(pattern.formatted(botName))))
                .findFirst()
                .orElse(null);
    }
}
