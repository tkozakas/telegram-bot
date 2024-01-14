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
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
public class MessageService {
    private final StatsService statsService;

    public MessageService(StatsService statsService) {
        this.statsService = statsService;
    }

    public Optional<SendMessage> processScheduledMessage() {
        log.info("Scheduled message");
        List<Stats> allStats = statsService.getAllStats();

        if (!allStats.isEmpty()) {
            Stats winner = allStats.get(ThreadLocalRandom.current().nextInt(allStats.size()));
            winner.setScore(winner.getScore() + 1);
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

        log.info("Message received: " + message + " from " + user.getFirstName());

        switch (message) {
            case "/pidoreg" -> {
                return createRegisterMessage(user, chatId);
            }
            case "/pidorstats" -> {
                List<Stats> stats = statsService.getStatsByChatIdAndYear(chatId, LocalDateTime.now().getYear());
                return createStatsMessage(stats, chatId, user.getFirstName());
            }
            case "/pidorall" -> {
                List<Stats> stats = statsService.getStatsByChatId(chatId);
                return createStatsMessage(stats, chatId, user.getFirstName());
            }
            case "/pidorme" -> {
                List<Stats> stats = statsService.getStatsByChatIdAndUserId(chatId, user.getId());
                return createStatsMessage(stats, chatId, user.getFirstName());
            }
            default -> log.error("Unknown command: " + message);
        }
        return processScheduledMessage();
    }

    private Optional<SendMessage> createRegisterMessage(User user, Long chatId) {
        if (!statsService.existsById(user.getId())) {
            statsService.addStat(new Stats(user.getId(), user.getFirstName(), chatId, 0L, LocalDateTime.now()));
            log.info("New user: " + user.getFirstName() + " (" + user.getId() + ")");
            return createMessage("You have been registered to the pidor game, " + user.getFirstName() + "!", chatId, user.getFirstName());

        } else {
            log.info("User: " + user.getFirstName() + " (" + user.getId() + ")", " is already registered");
            return createMessage("You are already in the pidor game, " + user.getFirstName() + "!", chatId, user.getFirstName());
        }
    }

    private Optional<SendMessage> createStatsMessage(List<Stats> stats, Long chatId, String firstName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Top Players of All Time:\n");

        int rank = 1;
        for (Stats stat : stats) {
            stringBuilder.append(rank++)
                    .append(". ")
                    .append(stat.getFirstName())
                    .append(" — ")
                    .append(stat.getScore())
                    .append(" times\n");
        }
        stringBuilder.append("\nTotal Participants — ")
                .append(stats.size());

        return createMessage(stringBuilder.toString(), chatId, firstName);
    }


    private Optional<SendMessage> createMessage(String s, Long chatId, String firstName) {
        log.info("Message sent: [" + s + "] to [" + firstName + " (" + chatId + ")]");
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(s);

        return Optional.of(sendMessage);
    }
}
