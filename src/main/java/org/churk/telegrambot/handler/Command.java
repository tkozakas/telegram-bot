package org.churk.telegrambot.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum Command {
    START(List.of(".*/start\\b.*"), "Activate bot"),
    HELP(List.of(".*/help\\b.*"), "Get help"),
    REGISTER(List.of(".*/%sreg\\b.*"), "Sign-up as %s"),
    DAILY_MESSAGE(List.of(".*/%s\\b.*"), "Get today's %s"),
    NEWS(List.of(".*/news\\b.*"), "Latest news. /news <query> for specifics"),
    STATS(List.of(".*/%sstats\\b.*"), "Statistics. /stats year or /stats user for details"),
    FACT(List.of(".*/fact\\b.*"), "Random Fact. /fact add to add a fact"),
    STICKER(List.of(".*/sticker\\b.*"), "Random Sticker. /sticker add <name>, /sticker remove <name>, /sticker list for management"),
    REDDIT(List.of(".*/reddit\\b.*", ".*/meme\\b.*"), "Random Reddit pic. /reddit <subreddit>, /reddit add <name> /reddit remove <name>, /reddit list for management");

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
