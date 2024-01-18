package org.churk.telegrampibot.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.churk.telegrampibot.builder.MessageBuilder;
import org.churk.telegrampibot.config.BotConfig;
import org.churk.telegrampibot.model.Sentence;
import org.churk.telegrampibot.model.Stats;
import org.churk.telegrampibot.utility.MemeDownloader;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.interfaces.Validable;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
public class MessageService {
    public static final Queue<Update> latestMessages = new CircularFifoQueue<>(3);
    private static final boolean ENABLED = true;
    private final BotConfig botConfig;
    private final StatsService statsService;
    private final MessageBuilder messageBuilder;
    private final StickerService stickerService;
    private final DailyMessageService dailyMessageService;
    private final FactService factService;

    public MessageService(BotConfig botConfig, StatsService statsService, org.churk.telegrampibot.utility.JSONLoader jsonLoader, MessageBuilder messageBuilder, MemeDownloader memeDownloader, StickerService stickerService, DailyMessageService dailyMessageService, FactService factService) {
        this.botConfig = botConfig;
        this.statsService = statsService;
        this.messageBuilder = messageBuilder;
        this.stickerService = stickerService;
        this.dailyMessageService = dailyMessageService;
        this.factService = factService;
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
            return messageBuilder.createPhotoMessage(messageIdToReply, downloadedFilePath, firstName, chatId);
        }
        log.error("Meme file was not downloaded in the given time");
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
            if (winner == null) {
                log.error("Winner exists but not found in the database");
                return List.of();
            }
            String winnerExistsMessage = dailyMessageService.getKeyNameSentence("key_name") + winner.getFirstName();
            return messageBuilder.createMessages(List.of(winnerExistsMessage), winner.getChatId(), winner.getFirstName());
        }
        Stats winner = allStats.get(ThreadLocalRandom.current().nextInt(allStats.size()));
        if (ENABLED) {
            winner.setScore(winner.getScore() + 1);
            winner.setIsWinner(Boolean.TRUE);
            statsService.updateStats(winner);
        }
        List<String> sentenceList = new ArrayList<>(dailyMessageService.getRandomGroupSentences().stream().map(Sentence::getText).toList());
        if (sentenceList.isEmpty()) {
            return List.of();
        }
        int lastSentenceIndex = sentenceList.size() - 1;
        sentenceList.set(lastSentenceIndex, sentenceList.get(lastSentenceIndex) + winner.getFirstName());
        return messageBuilder.createMessages(sentenceList, winner.getChatId(), winner.getFirstName());
    }

    private Validable processRandomFact(Update update, Optional<Integer> messageIdToReply) {
        Long chatId = update.getMessage().getChatId();
        String firstName = update.getMessage().getFrom().getFirstName();
        String randomFact = factService.getRandomFact();
        log.info("Sending fact: {}", randomFact);

        return messageBuilder.createMessage(randomFact, chatId, firstName, messageIdToReply);
    }

    private Validable processRandomSticker(Update update, Optional<Integer> messageIdToReply) {
        Long chatId = update.getMessage().getChatId();
        String firstName = update.getMessage().getFrom().getFirstName();
        String stickerId = stickerService.getRandomStickerId();
        log.info("Sending sticker: {}", stickerId);

        return messageBuilder.createStickerMessage(stickerId, chatId, firstName, messageIdToReply);
    }

    private Optional<Validable> processRandomSticker() {
        if (ThreadLocalRandom.current().nextInt(100) > 2) {
            return Optional.empty();
        }
        assert latestMessages.peek() != null;
        Message message = latestMessages.peek().getMessage();
        Optional<Integer> messageIdToReply = Optional.of(message.getMessageId());

        Long chatId = message.getChatId();
        String firstName = message.getFrom().getFirstName();
        String stickerId = stickerService.getRandomStickerId();
        log.info("Sending sticker: {}", stickerId);

        return Optional.of(messageBuilder.createStickerMessage(stickerId, chatId, firstName, messageIdToReply));
    }

    public void resetWinner() {
        List<Stats> allStats = statsService.getAllStats();
        allStats.forEach(stats -> stats.setIsWinner(Boolean.FALSE));
        statsService.updateStats(allStats);
    }

}
