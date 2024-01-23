package org.churk.telegrambot.builder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrambot.config.BotProperties;
import org.churk.telegrambot.model.Stats;
import org.churk.telegrambot.service.DailyMessageService;
import org.churk.telegrambot.service.StatsService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.interfaces.Validable;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageBuilder {
    private final StatsService statsService;
    private final DailyMessageService dailyMessageService;
    private final BotProperties botProperties;

    public Validable createStatsMessageForAll(Update update, Optional<Integer> messageIdToReply) {
        List<Stats> statsList = statsService.getAggregatedStatsByChatId(update.getMessage().getChatId());
        String header = dailyMessageService.getKeyNameSentence("stats_all_header").formatted(botProperties.getWinnerName());
        String footer = dailyMessageService.getKeyNameSentence("stats_footer") + statsList.size();

        return createStatsMessageForStat(statsList, update, header, footer, messageIdToReply);
    }

    public Validable createStatsMessageForYear(Update update, int year, Optional<Integer> messageIdToReply) {
        List<Stats> statsList = statsService.getStatsByChatIdAndYear(update.getMessage().getChatId(), year);
        String header;
        if (year == LocalDateTime.now().getYear()) {
            header = dailyMessageService.getKeyNameSentence("stats_now_header").formatted(botProperties.getWinnerName(), year);
        } else {
            header = dailyMessageService.getKeyNameSentence("stats_year_header").formatted(botProperties.getWinnerName(), year);
        }
        String footer = dailyMessageService.getKeyNameSentence("stats_footer") + statsList.size();

        return createStatsMessageForStat(statsList, update, header, footer, messageIdToReply);
    }

    public Validable createStatsMessageForUser(Update update, Optional<Integer> messageIdToReply) {
        User user = update.getMessage().getFrom();
        Long chatId = update.getMessage().getChatId();
        String firstName = update.getMessage().getFrom().getFirstName();

        List<Stats> statsByChatIdAndUserId = statsService.getStatsByChatIdAndUserId(chatId, user.getId());
        if (!statsByChatIdAndUserId.isEmpty()) {// get how many wons by now for this user
            long totalScore = statsByChatIdAndUserId.stream().mapToLong(Stats::getScore).sum();
            String header = dailyMessageService.getKeyNameSentence("me_header").formatted(firstName, botProperties.getWinnerName(), totalScore);
            return createMessage(header, chatId, firstName, messageIdToReply);
        }
        String header = dailyMessageService.getKeyNameSentence("not_registered_header") + firstName;
        return createMessage(header, chatId, firstName, messageIdToReply);
    }

    public Validable createRegisterMessage(Update update, Optional<Integer> messageIdToReply) {
        User user = update.getMessage().getFrom();
        String firstName = update.getMessage().getFrom().getFirstName();
        Long chatId = update.getMessage().getChatId();

        if (statsService.existsByUserId(user.getId())) {
            log.info("User: " + user.getFirstName() + " (" + user.getId() + ")", " is already registered");
            String header = dailyMessageService.getKeyNameSentence("registered_header") + firstName;
            return createMessage(header, chatId, firstName, messageIdToReply);
        }
        statsService.addStat(new Stats(UUID.randomUUID(), chatId, user.getId(), user.getFirstName(), 0L, LocalDateTime.now().getYear(), Boolean.FALSE));
        log.info("New user: " + user.getFirstName() + " (" + user.getId() + ")");
        String header = dailyMessageService.getKeyNameSentence("registered_now_header") + firstName;
        return createMessage(header, chatId, firstName, messageIdToReply);
    }

    public Validable createStatsMessageForStat(List<Stats> statsList, Update update, String header, String footer, Optional<Integer> messageIdToReply) {
        Long chatId = update.getMessage().getChatId();
        String firstName = update.getMessage().getFrom().getFirstName();

        List<Stats> modifiableList = new ArrayList<>(statsList);
        modifiableList.sort(Comparator.comparing(Stats::getScore).reversed());

        String stringBuilder = IntStream
                .iterate(0, i -> i < modifiableList.size() && i < 10, i -> i + 1)
                .mapToObj(i -> dailyMessageService.getKeyNameSentence("stats_table").
                        formatted(i + 1, modifiableList.get(i).getFirstName(), modifiableList.get(i).getScore()))
                .collect(Collectors.joining("", header, footer));

        return createMessage(stringBuilder, chatId, firstName, messageIdToReply);
    }

    public Validable createStickerMessage(String stickerId, Long chatId, String firstName, Optional<Integer> messageIdToReply) {
        log.info("Sticker sent: [%s] to [%s (%d)]".formatted(stickerId, firstName, chatId));
        SendSticker sendSticker = new SendSticker();
        sendSticker.setChatId(String.valueOf(chatId));
        sendSticker.setSticker(new InputFile(stickerId));
        messageIdToReply.ifPresent(sendSticker::setReplyToMessageId);
        return sendSticker;
    }

    public Validable createPhotoMessage(Optional<Integer> messageIdToReply, Long chatId, File file, Optional<String> caption) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(String.valueOf(chatId));
        sendPhoto.setPhoto(new InputFile(file));
        messageIdToReply.ifPresent(sendPhoto::setReplyToMessageId);
        caption.ifPresent(sendPhoto::setCaption);
        file.deleteOnExit();
        return sendPhoto;
    }

    public Validable createAnimationMessage(Optional<Integer> messageIdToReply, Long chatId, File file, Optional<String> caption) {
        SendAnimation sendAnimation = new SendAnimation();
        sendAnimation.setChatId(String.valueOf(chatId));
        sendAnimation.setAnimation(new InputFile(file));
        messageIdToReply.ifPresent(sendAnimation::setReplyToMessageId);
        caption.ifPresent(sendAnimation::setCaption);
        file.deleteOnExit();
        return sendAnimation;
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
}
