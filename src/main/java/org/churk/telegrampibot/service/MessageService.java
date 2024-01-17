package org.churk.telegrampibot.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.churk.telegrampibot.builder.MessageBuilder;
import org.churk.telegrampibot.config.BotConfig;
import org.churk.telegrampibot.model.Stats;
import org.churk.telegrampibot.utility.CSVLoader;
import org.churk.telegrampibot.utility.JSONLoader;
import org.churk.telegrampibot.utility.MemeDownloader;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.interfaces.Validable;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
public class MessageService {
    public static final Queue<Update> latestMessages = new CircularFifoQueue<>(3);
    private static final boolean ENABLED = false;
    private final BotConfig botConfig;
    private final StatsService statsService;
    private final CSVLoader csvLoader;
    private final JSONLoader jsonLoader;
    private final MessageBuilder messageBuilder;

    public MessageService(BotConfig botConfig, StatsService statsService, CSVLoader csvLoader, org.churk.telegrampibot.utility.JSONLoader jsonLoader, MessageBuilder messageBuilder, MemeDownloader memeDownloader) {
        this.botConfig = botConfig;
        this.statsService = statsService;
        this.csvLoader = csvLoader;
        this.jsonLoader = jsonLoader;
        this.messageBuilder = messageBuilder;
    }

    public List<Validable> handleCommand(Update update) {
        Optional<Integer> messageIdToReply = Optional.of(update.getMessage().getMessageId());
        List<Validable> response = new ArrayList<>();

        List<String> commandList = processMessage(update);
        String mainCommand = commandList.get(0);

        if (mainCommand.equals("/pidorstats") || mainCommand.equals("/pidorstats@" + botConfig.getUsername())) {
            response.add(handleStats(commandList, update, Optional.empty()).orElse(null));
        } else if (mainCommand.equals("/pidorall") || mainCommand.equals("/pidorall@" + botConfig.getUsername())) {
            response.add(messageBuilder.createStatsMessageForAll(update, Optional.empty()).orElse(null));
        } else if (mainCommand.equals("/pidorme") || mainCommand.equals("/pidorme@" + botConfig.getUsername())) {
            response.add(messageBuilder.createStatsMessageForUser(update, messageIdToReply));
        } else if (mainCommand.equals("/pidoreg") || mainCommand.equals("/pidoreg@" + botConfig.getUsername())) {
            response.add(messageBuilder.createRegisterMessage(update, messageIdToReply));
        } else if (mainCommand.contains("/fact") || mainCommand.equals("/fact@" + botConfig.getUsername())) {
            response.add(processRandomFact(update, Optional.empty()));
        } else if (mainCommand.contains("/pidor") || mainCommand.contains("/pidor@" + botConfig.getUsername())) {
            response.addAll(processDailyWinnerMessage());
        } else if (mainCommand.contains("/sticker") || mainCommand.equals("/sticker@" + botConfig.getUsername())) {
            response.add(processRandomSticker(update, messageIdToReply));
        } else if (mainCommand.contains("/meme") || mainCommand.equals("/meme@" + botConfig.getUsername())) {
            response.add(processRandomMeme(commandList, update, messageIdToReply).orElse(null));
        } else {
            response.add(processRandomSticker().orElse(null));
        }
        return response;
    }

    private Optional<Validable> processRandomMeme(List<String> commandList, Update update, Optional<Integer> messageIdToReply) {
        Long chatId = update.getMessage().getChatId();
        String firstName = update.getMessage().getFrom().getFirstName();
        String subreddit = (commandList.size() == 2) ? commandList.get(1) : null;

        log.info("Sending meme");
        MemeDownloader.downloadMeme(subreddit);
        String downloadedFilePath = MemeDownloader.waitForDownload();

        if (downloadedFilePath != null) {
            File memeFile = new File(downloadedFilePath);
            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(String.valueOf(chatId));
            sendPhoto.setPhoto(new InputFile(memeFile));
            sendPhoto.setCaption("Here's your meme, " + firstName);
            messageIdToReply.ifPresent(sendPhoto::setReplyToMessageId);
            memeFile.deleteOnExit();
            return Optional.of(sendPhoto);
        } else {
            log.error("Meme file was not downloaded in the given time");
        }
        return Optional.empty();

    }

    private Optional<Validable> handleStats(List<String> commandList, Update update, Optional<Integer> messageIdToReply) {
        if (commandList.isEmpty() || commandList.size() > 2) {
            log.error("Invalid command: {}", commandList);
            return Optional.empty();
        }
        int year = (commandList.size() == 2) ? Integer.parseInt(commandList.get(1)) : LocalDateTime.now().getYear();

        return messageBuilder.createStatsMessageForYear(update, year, messageIdToReply);
    }


    public List<String> processMessage(Update update) {
        String message = update.getMessage().getText();
        latestMessages.add(update);

        if (message.isBlank()) {
            log.info("Blank message received");
            return List.of();
        }

        return List.of(message.split(" "));
    }

    public List<Validable> processDailyWinnerMessage() {
        log.info("Scheduled message");
        List<Stats> allStats = statsService.getAllStats();

        if (allStats.isEmpty()) {
            log.info("No stats available to pick a winner.");
            return List.of();
        }
        if (statsService.existsWinnerToday()) {
            Stats winner = allStats.stream().filter(Stats::getIsWinner).findFirst().orElse(null);
            return processDailyMessage(winner, "winner_exists");
        }
        Stats winner = allStats.get(ThreadLocalRandom.current().nextInt(allStats.size()));
        if (ENABLED) {
            winner.setScore(winner.getScore() + 1);
            winner.setIsWinner(Boolean.TRUE);
            statsService.updateStats(winner);
        }
        return processDailyMessage(winner, "sentences");
    }

    private Validable processRandomFact(Update update, Optional<Integer> messageIdToReply) {
        Long chatId = update.getMessage().getChatId();
        String firstName = update.getMessage().getFrom().getFirstName();

        List<List<String>> records = csvLoader.readFromCSV("src/main/resources/facts.csv");
        List<String> randomFact = records.get(ThreadLocalRandom.current().nextInt(records.size()));
        return messageBuilder.createMessage(randomFact.get(0), chatId, firstName, messageIdToReply);
    }

    private Validable processRandomSticker(Update update, Optional<Integer> messageIdToReply) {
        Long chatId = update.getMessage().getChatId();
        String firstName = update.getMessage().getFrom().getFirstName();

        List<List<String>> records = csvLoader.readFromCSV("src/main/resources/stickers.csv");
        List<String> randomSticker = records.get(ThreadLocalRandom.current().nextInt(records.size()));
        String stickerId = randomSticker.get(0);
        log.info("Sending sticker: {}", stickerId);

        return messageBuilder.createStickerMessage(stickerId, chatId, firstName, messageIdToReply);
    }

    private Optional<Validable> processRandomSticker() {
        if (ThreadLocalRandom.current().nextInt(100) > 2) {
            return Optional.empty();
        }

        List<List<String>> records = csvLoader.readFromCSV("src/main/resources/stickers.csv");
        List<String> randomSticker = records.get(ThreadLocalRandom.current().nextInt(records.size()));
        String stickerId = randomSticker.get(0);
        log.info("Sending sticker: {}", stickerId);

        assert latestMessages.peek() != null;
        Message message = latestMessages.peek().getMessage();
        Optional<Integer> messageIdToReply = Optional.of(message.getMessageId());
        Long chatId = message.getChatId();
        String firstName = message.getFrom().getFirstName();

        return Optional.of(messageBuilder.createStickerMessage(stickerId, chatId, firstName, messageIdToReply));
    }

    private List<Validable> processDailyMessage(Stats winner, String key) {
        List<Map<String, Object>> jsonData = jsonLoader.readFromJSON("src/main/resources/daily-messages.json");
        List<Map<String, Object>> sentencesData = jsonData.stream()
                .filter(data -> data.containsKey(key))
                .toList();
        if (sentencesData.isEmpty()) {
            return Collections.emptyList();
        }
        if (key.equals("already_exists")) {
            String message = (String) sentencesData.get(0).get(key);
            return messageBuilder.createMessages(List.of(message + winner.getFirstName()), winner.getChatId(), winner.getFirstName());
        }

        List<List<String>> sentencesList = (List<List<String>>) sentencesData.get(0).get("sentences");
        List<String> randomSentences = sentencesList.get(ThreadLocalRandom.current().nextInt(sentencesList.size()));

        if (!randomSentences.isEmpty()) {
            int lastSentenceIndex = randomSentences.size() - 1;
            randomSentences.set(lastSentenceIndex, randomSentences.get(lastSentenceIndex) + winner.getFirstName());
        }
        return messageBuilder.createMessages(randomSentences, winner.getChatId(), winner.getFirstName());
    }

    public void resetWinner() {
        List<Stats> allStats = statsService.getAllStats();
        allStats.forEach(stats -> stats.setIsWinner(Boolean.FALSE));
        statsService.updateStats(allStats);
    }

}
