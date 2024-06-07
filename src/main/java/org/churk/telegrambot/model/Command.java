package org.churk.telegrambot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum Command {
    START(List.of(".*/start\\b.*"), List.of(SubCommand.NONE), "Activate bot"),
    HELP(List.of(".*/help\\b.*"), List.of(SubCommand.NONE), "Get help"),
    DAILY_MESSAGE(List.of(".*/%s\\b.*"), List.of(SubCommand.ALL, SubCommand.STATS, SubCommand.YEAR), "Daily game"),
    NEWS(List.of(".*/news\\b.*"), List.of(SubCommand.NONE), "Latest news"),
    FACT(List.of(".*/fact\\b.*"), List.of(SubCommand.ADD), "Random fact"),
    STICKER(List.of(".*/sticker\\b.*"), List.of(SubCommand.ADD, SubCommand.REMOVE, SubCommand.LIST), "Manage stickers"),
    REDDIT(List.of(".*/reddit\\b.*", ".*/meme\\b.*"), List.of(SubCommand.ADD, SubCommand.REMOVE, SubCommand.LIST), "Reddit pics"),
    SHITPOST(List.of(".*/shitpost\\b.*"), List.of(SubCommand.NONE), "Random shitpost"),
    TTS(List.of(".*/tts\\b.*"), List.of(SubCommand.NONE), "Text to speech"),
    NONE(List.of(""), List.of(SubCommand.NONE), "");

    private final List<String> patterns;
    private final List<SubCommand> subCommands;
    private final String description;

    public static Command getTextCommand(String text, String botName) {
        String command = String.valueOf(Arrays.stream(text.split(" ")).findFirst());
        return Arrays.stream(Command.values())
                .filter(c -> c.getPatterns().stream()
                        .anyMatch(pattern -> {
                            Pattern compiledPattern = Pattern.compile(pattern.formatted(botName), Pattern.DOTALL);
                            Matcher matcher = compiledPattern.matcher(command);
                            return matcher.find();
                        }))
                .findFirst()
                .orElse(NONE);
    }

    public String getSubCommandsString() {
        return getSubCommands().stream()
                .filter(subCommand -> subCommand != SubCommand.NONE)
                .map(subCommand -> subCommand.getCommand().getFirst())
                .collect(Collectors.joining(","));
    }

    public String getPatternCleaned(String botName) {
        return getPatterns().getFirst().formatted(botName)
                .replace(".*/", "/")
                .replace("\\b.*", "");
    }

    public String getPatternCleaned() {
        return getPatterns().getFirst()
                .replace(".*/", "/")
                .replace("\\b.*", "");
    }
}
