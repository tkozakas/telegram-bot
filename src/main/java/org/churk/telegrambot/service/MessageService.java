package org.churk.telegrambot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.churk.telegrambot.builder.MessageBuilder;
import org.churk.telegrambot.config.BotProperties;
import org.churk.telegrambot.config.MemeProperties;
import org.churk.telegrambot.model.Stats;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.interfaces.Validable;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {
    protected static final Queue<Update> latestMessages = new CircularFifoQueue<>(3);
    private static final boolean ENABLED = true;
    private final MemeProperties memeProperties;
    private final MessageBuilder messageBuilder;
    private final StatsService statsService;
    private final StickerService stickerService;
    private final DailyMessageService dailyMessageService;
    private final FactService factService;
    private final MemeService memeService;
    private final BotProperties botProperties;

    public List<Validable> handleCommand(Update update) {
        Optional<Integer> messageIdToReply = Optional.of(update.getMessage().getMessageId());
        List<Validable> response = new ArrayList<>();
        List<String> commandList = processMessage(update);

        Map<Supplier<Optional<Validable>>, List<String>> commandHandlers = Map.of(
                // Create Register Message
                () -> Optional.of(messageBuilder.createRegisterMessage(update, messageIdToReply)),
                List.of(".*/%sreg\\b.*".formatted(botProperties.getWinnerName())),

                // Handle Stats
                () -> handleStats(commandList, update, Optional.empty()),
                List.of(".*/%sstats\\b.*".formatted(botProperties.getWinnerName())),

                // Create Stats Message for All
                () -> Optional.of(messageBuilder.createStatsMessageForAll(update, Optional.empty())),
                List.of(".*/%sall\\b.*".formatted(botProperties.getWinnerName())),

                // Create Stats Message for User
                () -> Optional.of(messageBuilder.createStatsMessageForUser(update, messageIdToReply)),
                List.of(".*/%sme\\b.*".formatted(botProperties.getWinnerName())),

                // Process Random Fact
                () -> Optional.of(processRandomFact(update, Optional.empty())),
                List.of(".*/fact\\b.*"),

                // Process Random Sticker
                () -> Optional.of(processRandomSticker(update, Optional.empty())),
                List.of(".*/sticker\\b.*"),

                // Process Daily Winner Message
                () -> {
                    response.addAll(processDailyWinnerMessage());
                    return Optional.empty();
                },
                List.of(".*/%s\\b.*".formatted(botProperties.getWinnerName())),

                // Process Random Meme
                () -> {
                    List<Validable> memeResponse = processRandomMeme(commandList, update, Optional.empty());
                    return memeResponse.isEmpty() ? Optional.empty() : Optional.ofNullable(memeResponse.get(0));
                },
                List.of(".*/meme.*")
        );

        Optional<Supplier<Optional<Validable>>> commandHandler = commandHandlers.entrySet().stream()
                .filter(entry -> entry.getValue().stream().anyMatch(commandList.toString()::matches))
                .map(Map.Entry::getKey)
                .findFirst();

        commandHandler.orElse(this::processRandomResponse)
                .get()
                .ifPresent(response::add);

        return response;
    }

    private Optional<Validable> processRandomResponse() {
        List<Supplier<Optional<Validable>>> randomResponseHandlers = List.of(
                this::processRandomSticker,
                this::processRandomFact
        );

        if (ThreadLocalRandom.current().nextInt(100) > 2) {
            return Optional.empty();
        }
        return randomResponseHandlers.get(ThreadLocalRandom.current().nextInt(randomResponseHandlers.size())).get();
    }

    private Optional<Validable> processRandomFact() {
        assert latestMessages.peek() != null;
        return Optional.of(processRandomFact(latestMessages.peek(), Optional.of(latestMessages.peek().getMessage().getMessageId())));
    }

    public List<Validable> processRandomMeme(List<String> commandList, Update update, Optional<Integer> messageIdToReply) {
        Long chatId = update.getMessage().getChatId();
        String subreddit = (commandList.size() == 2) ? commandList.get(1) : null;
        Optional<File> memeFile;
        try {
            memeFile = retrieveMeme(subreddit);
            if (memeFile.isPresent()) {
                Optional<String> caption = Optional.of("From r/%s".formatted(subreddit));
                String filename = memeFile.get().getName().toLowerCase();
                Optional<String> isSubreddit = subreddit == null ? Optional.empty() : caption;
                return filename.endsWith(".gif") ?
                        List.of(messageBuilder.createAnimationMessage(messageIdToReply, chatId, memeFile.get(), isSubreddit)) :
                        List.of(messageBuilder.createPhotoMessage(messageIdToReply, chatId, memeFile.get(), isSubreddit));
            }
        } catch (feign.FeignException.NotFound e) {
            log.error("Subreddit not found: {}", e.getMessage());
            return List.of(messageBuilder.createMessage("Subreddit does not exist", chatId, update.getMessage().getFrom().getFirstName(), messageIdToReply));
        } catch (Exception e) {
            log.error("Error fetching meme from subreddit: {}", e.getMessage());
            return List.of(messageBuilder.createMessage("Error fetching meme from subreddit", chatId, update.getMessage().getFrom().getFirstName(), messageIdToReply));
        }
        return List.of();
    }

    private Optional<File> retrieveMeme(String subreddit) throws feign.FeignException.NotFound {
        if (subreddit != null) {
            return memeService.getMemeFromSubreddit(subreddit);
        }
            log.info("Sending random meme");
            return memeService.getMeme();
    }

    public List<Validable> processScheduledRandomMeme() {
        assert latestMessages.peek() != null;
        String subreddit = memeProperties.getScheduledSubreddits().get(ThreadLocalRandom.current().nextInt(memeProperties.getScheduledSubreddits().size()));
        return List.of(processRandomMeme(List.of(subreddit, subreddit), latestMessages.peek(), Optional.empty()).get(0));
    }

    private Optional<Validable> handleStats(List<String> commandList, Update update, Optional<Integer> messageIdToReply) {
        if (commandList.isEmpty() || commandList.size() > 2) {
            log.error("Invalid command: {}", commandList);
            return Optional.empty();
        }
        int year = (commandList.size() == 2) ? Integer.parseInt(commandList.get(1)) : LocalDateTime.now().getYear();

        return Optional.of(messageBuilder.createStatsMessageForYear(update, year, messageIdToReply));
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

        if (statsService.existsByWinnerToday()) {
            return handleExistingWinner(allStats);
        }

        return handleNewWinner(allStats);
    }

    private List<Validable> handleExistingWinner(List<Stats> allStats) {
        Stats winner = allStats.stream().filter(Stats::getIsWinner).findFirst().orElse(null);
        if (winner == null) {
            log.error("Winner exists but not found in the database");
            return List.of();
        }
        String winnerExistsMessage = dailyMessageService.getKeyNameSentence("key_name").formatted(botProperties.getWinnerName(), winner.getFirstName());
        return messageBuilder.createMessages(List.of(winnerExistsMessage), winner.getChatId(), winner.getFirstName());
    }

    private List<Validable> handleNewWinner(List<Stats> allStats) {
        Stats winner = allStats.get(ThreadLocalRandom.current().nextInt(allStats.size()));
        if (ENABLED) {
            winner.setScore(winner.getScore() + 1);
            winner.setIsWinner(Boolean.TRUE);
            statsService.updateStats(winner);
        }
        List<String> sentenceList = new ArrayList<>(dailyMessageService.getRandomGroupSentences().stream()
                .map(sentence -> sentence.getText()
                        .formatted(botProperties.getWinnerName()))
                .toList());
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

        return messageBuilder.createMessage(Objects.requireNonNullElse(randomFact, "No facts found in database"), chatId, firstName, messageIdToReply);
    }

    private Validable processRandomSticker(Update update, Optional<Integer> messageIdToReply) {
        Long chatId = update.getMessage().getChatId();
        String firstName = update.getMessage().getFrom().getFirstName();
        String stickerId = stickerService.getRandomStickerId();
        log.info("Sending sticker: {}", stickerId);

        return messageBuilder.createStickerMessage(stickerId, chatId, firstName, messageIdToReply);
    }

    private Optional<Validable> processRandomSticker() {
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
