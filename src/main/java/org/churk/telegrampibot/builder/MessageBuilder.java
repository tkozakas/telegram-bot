package org.churk.telegrampibot.builder;

import lombok.extern.slf4j.Slf4j;
import org.churk.telegrampibot.config.BotConfig;
import org.churk.telegrampibot.model.Stats;
import org.churk.telegrampibot.service.StatsService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.interfaces.Validable;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
public class MessageBuilder {
    private final BotConfig botConfig;
    private final StatsService statsService;

    public MessageBuilder(BotConfig botConfig, StatsService statsService) {
        this.botConfig = botConfig;
        this.statsService = statsService;
    }

    public Optional<Validable> createStatsMessageForAll(Update update, Optional<Integer> messageIdToReply) {
        List<Stats> statsList = statsService.getAggregatedStatsByChatId(update.getMessage().getChatId());
        String header = String.format("*All time " + botConfig.getWinnerName() + "s*%n%n");
        String footer = String.format("%n*Total Participants — %d*", statsList.size());

        return createStatsMessageForStat(statsList, update, header, footer, messageIdToReply);
    }

    public Optional<Validable> createStatsMessageForYear(Update update, int year, Optional<Integer> messageIdToReply) {
        List<Stats> statsList = statsService.getStatsByChatIdAndYear(update.getMessage().getChatId(), year);
        String header = String.format("*" + botConfig.getWinnerName() + "s of %d*%n%n", year);
        String footer = String.format("%n*Total Participants — %d*", statsList.size());

        return createStatsMessageForStat(statsList, update, header, footer, messageIdToReply);
    }

    public Optional<Validable> createStatsMessageForStat(List<Stats> statsList, Update update, String header, String footer, Optional<Integer> messageIdToReply) {
        Long chatId = update.getMessage().getChatId();
        String firstName = update.getMessage().getFrom().getFirstName();

        List<Stats> modifiableList = new ArrayList<>(statsList);
        modifiableList.sort(Comparator.comparing(Stats::getScore).reversed());
        int maxIndexLength = String.valueOf(Math.min(modifiableList.size(), 10)).length();

        String stringBuilder = IntStream
                .iterate(0, i -> i < modifiableList.size() && i < 10, i -> i + 1)
                .mapToObj(i -> String.format("%" + maxIndexLength + "d. *%s* — *%d* times%n",
                        i + 1,
                        modifiableList.get(i).getFirstName(),
                        modifiableList.get(i).getScore()))
                .collect(Collectors.joining("", header, footer));

        return Optional.of(createMessage(stringBuilder, chatId, firstName, messageIdToReply));
    }

    public Validable createStickerMessage(String stickerId, Long chatId, String firstName, Optional<Integer> messageIdToReply) {
        log.info("Sticker sent: [%s] to [%s (%d)]".formatted(stickerId, firstName, chatId));
        SendSticker sendSticker = new SendSticker();
        sendSticker.setChatId(String.valueOf(chatId));
        sendSticker.setSticker(new InputFile(stickerId));
        messageIdToReply.ifPresent(sendSticker::setReplyToMessageId);
        return sendSticker;
    }


    public Validable createMessage(String s, Long chatId, String firstName, Optional<Integer> messageIdToReply) {
        SendMessage sendMessage = (SendMessage) createMessage(s, chatId, firstName);
        messageIdToReply.ifPresent(sendMessage::setReplyToMessageId);
        return sendMessage;
    }

    public Validable createMessage(String s, Long chatId, String firstName) {
        log.info("Message sent: [" + s.replace("\n", "") + "] to [" + firstName + " (" + chatId + ")]");
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setParseMode("Markdown");
        sendMessage.enableMarkdown(true);
        sendMessage.setText(s);
        return sendMessage;
    }

    public List<Validable> createMessages(List<String> s, Long chatId, String firstName) {
        s.forEach(str -> str.replace("\n", ""));
        List<Validable> sendMessage = new ArrayList<>();
        s.forEach(str -> sendMessage.add(createMessage(str, chatId, firstName)));
        return sendMessage;
    }

    public Validable createStatsMessageForUser(Update update, Optional<Integer> messageIdToReply) {
        User user = update.getMessage().getFrom();
        Long chatId = update.getMessage().getChatId();
        String firstName = update.getMessage().getFrom().getFirstName();

        List<Stats> statsByChatIdAndUserId = statsService.getStatsByChatIdAndUserId(chatId, user.getId());
        if (!statsByChatIdAndUserId.isEmpty()) {
            Stats stats = statsByChatIdAndUserId.get(0);
            return createMessage("You have been *" + botConfig.getWinnerName() + "* " + stats.getScore() + " times, " + firstName + "!", chatId, firstName, messageIdToReply);
        }
        return createMessage("You are not registered for the *%s* game, *%s*!".formatted(botConfig.getWinnerName(), user.getFirstName()), chatId, firstName, messageIdToReply);
    }

    public Validable createRegisterMessage(Update update, Optional<Integer> messageIdToReply) {
        User user = update.getMessage().getFrom();
        String firstName = update.getMessage().getFrom().getFirstName();
        Long chatId = update.getMessage().getChatId();

        if (statsService.existsByUserId(user.getId())) {
            log.info("User: " + user.getFirstName() + " (" + user.getId() + ")", " is already registered");
            return createMessage("You are already in the " + botConfig.getWinnerName() + " game, " + firstName + "!", chatId, firstName, messageIdToReply);
        }
        statsService.addStat(new Stats(UUID.randomUUID(), chatId, user.getId(), user.getFirstName(), 0L, LocalDateTime.now(), Boolean.FALSE));
        log.info("New user: " + user.getFirstName() + " (" + user.getId() + ")");
        return createMessage("You have been registered to the " + botConfig.getWinnerName() + " game, " + firstName + "!", chatId, firstName, messageIdToReply);
    }
}
