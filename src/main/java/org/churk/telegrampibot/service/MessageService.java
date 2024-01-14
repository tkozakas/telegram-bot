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

        log.info("Message received: " + message + " from " + user.getFirstName());

        if (message.contains("/pidoreg")) {
            return createRegisterMessage(user, chatId);
        } else if (message.equals("/pidor") || message.equals("/pidor@PidorSheepLoverBot")) {
            return processScheduledMessage();
        } else if (message.contains("/pidorstats")) {
            List<Stats> stats = statsService.getStatsByChatIdAndYear(chatId, LocalDateTime.now().getYear());
            return createStatsMessage(stats, chatId, user.getFirstName());
        } else if (message.contains("/pidorall")) {
            List<Stats> stats = statsService.getStatsByChatId(chatId);
            return createStatsMessage(stats, chatId, user.getFirstName());
        } else if (message.contains("/pidorme")) {
            List<Stats> stats = statsService.getStatsByChatIdAndUserId(chatId, user.getId());
            return createStatsMessage(stats, chatId, user.getFirstName());
        } else {
            log.error("Unknown command: " + message);
        }
        return Optional.empty();
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

    private Optional<SendMessage> createStatsMessage(List<Stats> stats, Long chatId, String firstName) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < stats.size() && i < 10; i++) {
            stringBuilder.append(i + 1)
                    .append(". ")
                    .append(stats.get(i).getFirstName())
                    .append(" — ")
                    .append(stats.get(i).getScore())
                    .append(" times\n");
        }
        stringBuilder.append("\nTotal Participants — ")
                .append(stats.size());

        return createMessage(stringBuilder.toString(), chatId, firstName);
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
