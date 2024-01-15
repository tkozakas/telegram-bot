package org.churk.telegrampibot.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.churk.telegrampibot.config.BotConfig;
import org.churk.telegrampibot.csv.CSVLoader;
import org.churk.telegrampibot.model.Stats;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
public class MessageService {
    private static Queue<Update> latestMessages = new CircularFifoQueue<>(5);
    private final BotConfig botConfig;
    private final StatsService statsService;
    private final CSVLoader csvLoader;

    public MessageService(BotConfig botConfig, StatsService statsService, CSVLoader csvLoader) {
        this.botConfig = botConfig;
        this.statsService = statsService;
        this.csvLoader = csvLoader;
    }

    public Optional<SendMessage> processScheduledMessage() {
        log.info("Scheduled message");
        List<Stats> allStats = statsService.getAllStats();

        if (allStats.isEmpty() || statsService.existsWinnerToday()) {
            log.info("No stats available to pick a winner.");
            return Optional.empty();
        }
        Stats winner = allStats.get(ThreadLocalRandom.current().nextInt(allStats.size()));
        winner.setScore(winner.getScore() + 1);
        winner.setIsWinner(Boolean.TRUE);
        statsService.updateStats(winner);
        String messageText = String.format("Today's pidoras is %s!", winner.getFirstName() + "%n" +
                "Total pidoras count: " + winner.getScore());

        return createMessage(messageText, winner.getChatId(), winner.getFirstName());
    }

    private Optional<SendMessage> handleCommand(List<String> commandList, User user, Long chatId) {
        String mainCommand = commandList.get(0);
        String botUsername = "@" + botConfig.getUsername();
        if (mainCommand.equals("/pidorstats") || mainCommand.equals("/pidorstats" + botUsername)) {
            return handlePidorStats(commandList, chatId, user);
        } else if (mainCommand.equals("/pidoreg") || mainCommand.equals("/pidoreg" + botUsername)) {
            return createRegisterMessage(user, chatId);
        } else if (mainCommand.equals("/pidorall") || mainCommand.equals("/pidorall" + botUsername)) {
            return createStatsMessageForAll(user, chatId);
        } else if (mainCommand.equals("/pidorme") || mainCommand.equals("/pidorme" + botUsername)) {
            return createStatsMessageForUser(chatId, user);
        } else if (mainCommand.contains("/pidor") || mainCommand.equals("/pidor" + botUsername)) {
            return processWinnerOfTheDay();
        } else if (mainCommand.contains("/fact") || mainCommand.equals("/fact" + botUsername)) {
            return processRandomFact(chatId, user);
        }
        log.error("Unknown command: {}", mainCommand);
        return Optional.empty();
    }

    public Optional<SendSticker> processSticker(Update update) {
        String message = update.getMessage().getText();
        String botUsername = "@" + botConfig.getUsername();
        if (message.contains("/sticker") || message.equals("/sticker" + botUsername)) {
            return processRandomSticker();
        }
        if (ThreadLocalRandom.current().nextInt(100) > 10) {
            return Optional.empty();
        }
        return processRandomSticker();
    }

    private Optional<SendSticker> processRandomSticker() {
        List<List<String>> records = csvLoader.readFromCSV("src/main/resources/stickers.csv");
        List<String> randomSticker = records.get(ThreadLocalRandom.current().nextInt(records.size()));
        String stickerId = randomSticker.get(0);
        log.info("Sending sticker: {}", stickerId);
        assert latestMessages.peek() != null;
        Message message = latestMessages.peek().getMessage();
        return createStickerMessage(stickerId, message.getChatId(), message.getFrom().getFirstName());
    }

    public Optional<SendMessage> processWinnerOfTheDay() {
        List<Stats> allStats = statsService.getAllStats();
        if (!statsService.existsWinnerToday()) {
            return Optional.empty();
        }
        Stats winner = allStats.stream().filter(Stats::getIsWinner).findFirst().orElse(null);
        assert winner != null;
        String messageText = String.format("Today's pidoras is %s with the total count of: %d", winner.getFirstName(), winner.getScore());
        return createMessage(messageText, winner.getChatId(), winner.getFirstName());
    }

    public Optional<SendMessage> processMessage(Update update) {
        User user = update.getMessage().getFrom();
        String message = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();

        log.info("Message received: {} from {}", message, user.getFirstName());
        latestMessages.add(update);

        if (message.isBlank()) {
            log.info("Blank message received");
            return Optional.empty();
        }

        List<String> commandList = List.of(message.split(" "));
        return handleCommand(commandList, user, chatId);
    }

    private Optional<SendSticker> createStickerMessage(String stickerId, Long chatId, String firstName) {
        log.info("Sticker sent: [" + stickerId + "] to [" + firstName + " (" + chatId + ")]");
        SendSticker sendSticker = new SendSticker();
        sendSticker.setChatId(String.valueOf(chatId));
        sendSticker.setSticker(new InputFile(stickerId));
        assert latestMessages.peek() != null;
        sendSticker.setReplyToMessageId(latestMessages.peek().getMessage().getMessageId());

        return Optional.of(sendSticker);
    }

    private Optional<SendMessage> processRandomFact(Long chatId, User user) {
        List<List<String>> records = csvLoader.readFromCSV("src/main/resources/facts.csv");
        List<String> randomFact = records.get(ThreadLocalRandom.current().nextInt(records.size()));
        return createMessage(randomFact.get(0), chatId, user.getFirstName());
    }

    private Optional<SendMessage> createStatsMessageForUser(Long chatId, User user) {
        List<Stats> statsByChatIdAndUserId = statsService.getStatsByChatIdAndUserId(chatId, user.getId());
        if (!statsByChatIdAndUserId.isEmpty()) {
            Stats stats = statsByChatIdAndUserId.get(0);
            return createMessage("You have been pidor " + stats.getScore() + " times, " + user.getFirstName() + "!", chatId, user.getFirstName());
        }
        return createMessage("You are not registered for the pidor game, " + user.getFirstName() + "!", chatId, user.getFirstName());
    }

    private Optional<SendMessage> handlePidorStats(List<String> commandList, Long chatId, User user) {
        switch (commandList.size()) {
            case 1 -> {
                List<Stats> statsList = statsService.getStatsByChatIdAndYear(chatId, LocalDateTime.now().getYear());
                return createStatsMessageForYear(statsList, chatId, user.getFirstName());
            }
            case 2 -> {
                try {
                    int year = Integer.parseInt(commandList.get(1));
                    List<Stats> statsList = statsService.getStatsByChatIdAndYear(chatId, year);
                    return createStatsMessageForYear(statsList, chatId, user.getFirstName());
                } catch (NumberFormatException e) {
                    log.error("Invalid year: {}", commandList.get(1));
                    return Optional.empty();
                }
            }
            default -> {
                log.error("Invalid command: {}", commandList);
                return Optional.empty();
            }
        }
    }

    private Optional<SendMessage> createRegisterMessage(User user, Long chatId) {
        if (statsService.existsByUserId(user.getId())) {
            log.info("User: " + user.getFirstName() + " (" + user.getId() + ")", " is already registered");
            return createMessage("You are already in the pidor game, " + user.getFirstName() + "!", chatId, user.getFirstName());
        }
        statsService.addStat(new Stats(UUID.randomUUID(), chatId, user.getId(), user.getFirstName(), 0L, LocalDateTime.now(), Boolean.FALSE));
        log.info("New user: " + user.getFirstName() + " (" + user.getId() + ")");
        return createMessage("You have been registered to the pidor game, " + user.getFirstName() + "!", chatId, user.getFirstName());

    }

    private Optional<SendMessage> createStatsMessageForStat(List<Stats> statsList, Long chatId, String firstName, String header, String footer) {
        List<Stats> modifiableList = new ArrayList<>(statsList);
        modifiableList.sort(Comparator.comparing(Stats::getScore).reversed());
        int maxIndexLength = String.valueOf(Math.min(modifiableList.size(), 10)).length();

        String stringBuilder = IntStream
                .iterate(0, i -> i < modifiableList.size() && i < 10, i -> i + 1)
                .mapToObj(i -> String.format("%" + maxIndexLength + "d. %s — %d times%n",
                        i + 1,
                        modifiableList.get(i).getFirstName(),
                        modifiableList.get(i).getScore()))
                .collect(Collectors.joining("", header, footer));

        return createMessage(stringBuilder, chatId, firstName);
    }

    private Optional<SendMessage> createStatsMessageForAll(User user, Long chatId) {
        List<Stats> statsList = statsService.getAggregatedStatsByChatId(chatId);
        String header = String.format("**All time pidors**%n%n");
        String footer = String.format("%n**Total Participants — %d**", statsList.size());

        return createStatsMessageForStat(statsList, chatId, user.getFirstName(), header, footer);
    }

    private Optional<SendMessage> createStatsMessageForYear(List<Stats> statsList, Long chatId, String firstName) {
        int year = statsList.get(0).getCreatedAt().getYear();
        String header = String.format("**Pidors of %d**%n%n", year);
        String footer = String.format("%n**Total Participants — %d**", statsList.size());

        return createStatsMessageForStat(statsList, chatId, firstName, header, footer);
    }


    private Optional<SendMessage> createMessage(String s, Long chatId, String firstName) {
        log.info("Message sent: [" + s.replace("\n", "") + "] to [" + firstName + " (" + chatId + ")]");
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setParseMode("Markdown");
        sendMessage.setText(s);

        return Optional.of(sendMessage);
    }

    public void resetWinner() {
        List<Stats> allStats = statsService.getAllStats();
        allStats.forEach(stats -> stats.setIsWinner(Boolean.FALSE));
        statsService.updateStats(allStats);
    }
}
