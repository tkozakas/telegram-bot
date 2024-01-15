package org.churk.telegrampibot.service;

import lombok.extern.slf4j.Slf4j;
import org.churk.telegrampibot.model.Stats;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
public class MessageService {
    private final StatsService statsService;

    public MessageService(StatsService statsService) {
        this.statsService = statsService;
    }

    public Optional<SendMessage> processScheduledMessage() {
        log.info("Scheduled message");
        List<Stats> allStats = statsService.getAllStats();
        if (statsService.existsWinnerToday()) {
            Stats winner = allStats.stream().filter(Stats::getIsWinner).findFirst().orElse(null);
            String messageText = String.format("Today's pidoras is %s!", winner.getFirstName(), winner.getScore()) + "\n" +
                    "Total pidoras count: " + winner.getScore();
            return createMessage(messageText, winner.getChatId(), winner.getFirstName());
        }
        if (!allStats.isEmpty()) {
            Stats winner = allStats.get(ThreadLocalRandom.current().nextInt(allStats.size()));
            winner.setScore(winner.getScore() + 1);
            winner.setIsWinner(Boolean.TRUE);
            statsService.updateStats(winner);
            String messageText = String.format("Today's pidoras is %s!", winner.getFirstName(), winner.getScore()) + "\n" +
                    "Total pidoras count: " + winner.getScore();

            return createMessage(messageText, winner.getChatId(), winner.getFirstName());
        } else {
            log.info("No stats available to pick a winner.");
        }
        return Optional.empty();
    }

    public Optional<SendMessage> processMessage(Update update) {
        User user = update.getMessage().getFrom();
        String message = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();

        log.info("Message received: {} from {}", message, user.getFirstName());

        if (message.isBlank()) {
            log.info("Blank message received");
            return Optional.empty();
        }

        List<String> commandList = List.of(message.split(" "));
        return handleCommand(commandList, user, chatId);
    }

    private Optional<SendMessage> handleCommand(List<String> commandList, User user, Long chatId) {
        String mainCommand = commandList.get(0);
        switch (mainCommand) {
            case "/pidorstats":
                return handlePidorStats(commandList, chatId, user);
            case "/pidoreg":
                return createRegisterMessage(user, chatId);
            case "/pidor":
                return processScheduledMessage();
            case "/pidorall":
                return createStatsMessageForAll(user, chatId);
            case "/pidorme":
                return createStatsMessageForUser(chatId, user);
            default:
                log.error("Unknown command: {}", mainCommand);
                return Optional.empty();
        }
    }

    private Optional<SendMessage> createStatsMessageForUser(Long chatId, User user) {
        List<Stats> statsByChatIdAndUserId = statsService.getStatsByChatIdAndUserId(chatId, user.getId());
        if (statsByChatIdAndUserId.isEmpty()) {
            return createMessage("You are not registered for the pidor game, " + user.getFirstName() + "!", chatId, user.getFirstName());
        } else {
            Stats stats = statsByChatIdAndUserId.get(0);
            return createMessage("You have been pidor " + stats.getScore() + " times, " + user.getFirstName() + "!", chatId, user.getFirstName());
        }
    }

    private Optional<SendMessage> handlePidorStats(List<String> commandList, Long chatId, User user) {
        switch (commandList.size()) {
            case 1 -> {
                List<Stats> statsList = statsService.getStatsByChatIdAndYear(chatId, LocalDateTime.now().getYear());
                return createStatsMessageForStat(statsList, chatId, user.getFirstName());
            }
            case 2 -> {
                try {
                    int year = Integer.parseInt(commandList.get(1));
                    List<Stats> statsList = statsService.getStatsByChatIdAndYear(chatId, year);
                    return createStatsMessageForStat(statsList, chatId, user.getFirstName());
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
        if (!statsService.existsByUserId(user.getId())) {
            statsService.addStat(new Stats(UUID.randomUUID(), chatId, user.getId(), user.getFirstName(), 0L, LocalDateTime.now(), Boolean.FALSE));
            log.info("New user: " + user.getFirstName() + " (" + user.getId() + ")");
            return createMessage("You have been registered to the pidor game, " + user.getFirstName() + "!", chatId, user.getFirstName());
        } else {
            log.info("User: " + user.getFirstName() + " (" + user.getId() + ")", " is already registered");
            return createMessage("You are already in the pidor game, " + user.getFirstName() + "!", chatId, user.getFirstName());
        }
    }

    private Optional<SendMessage> createStatsMessageForStat(List<Stats> statsList, Long chatId, String firstName) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < statsList.size() && i < 10; i++) {
            stringBuilder.append(i + 1)
                    .append(". ")
                    .append(statsList.get(i).getFirstName())
                    .append(" — ")
                    .append(statsList.get(i).getScore())
                    .append(" times\n");
        }
        stringBuilder.append("\nTotal Participants — ")
                .append(statsList.size());

        return createMessage(stringBuilder.toString(), chatId, firstName);
    }

    private Optional<SendMessage> createStatsMessageForAll(User user, Long chatId) {
        List<Stats> statsList = statsService.getStatsByChatId(chatId);
        return createStatsMessageForStat(statsList, chatId, user.getFirstName());
    }


    private Optional<SendMessage> createMessage(String s, Long chatId, String firstName) {
        log.info("Message sent: [" + s.replace("\n", "") + "] to [" + firstName + " (" + chatId + ")]");
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(s);

        return Optional.of(sendMessage);
    }

    public void resetWinner() {
        List<Stats> allStats = statsService.getAllStats();
        allStats.forEach(stats -> stats.setIsWinner(Boolean.FALSE));
        statsService.updateStats(allStats);
    }
}
