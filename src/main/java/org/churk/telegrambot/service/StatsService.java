package org.churk.telegrambot.service;

import lombok.RequiredArgsConstructor;
import org.churk.telegrambot.model.Stats;
import org.churk.telegrambot.repository.StatsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsService {
    private final StatsRepository statsRepository;


    public Optional<Stats> getStatsByChatIdAndUserId(Long chatId, Long userId) {
        return statsRepository.getStatsByChatIdAndUserId(chatId, userId);
    }

    public void registerByUserIdAndChatId(Long userId, Long chatId, String firstName) {
        Stats stats = new Stats();
        stats.setUserId(userId);
        stats.setChatId(chatId);
        stats.setFirstName(firstName);
        stats.setYear(LocalDateTime.now().getYear());
        statsRepository.save(stats);
    }

    public List<Stats> getStatsByChatIdAndYear(Long chatId, int year) {
        return statsRepository.getStatsByChatIdAndYear(chatId, year);
    }

    public void updateStats(Stats randomWinner) {
        randomWinner.setScore(randomWinner.getScore() + 1);
        randomWinner.setIsWinner(Boolean.TRUE);
        statsRepository.save(randomWinner);
    }

    public List<Stats> getAllStatsByChatIdAndYear(Long chatId, int year) {
        return statsRepository.findAllByChatIdAndYear(chatId, year);
    }

    public List<Stats> getAllStatsByChatId(Long chatId) {
        return statsRepository.findAllByChatId(chatId).stream()
                .collect(Collectors.groupingBy(Stats::getUserId))
                .entrySet().stream()
                .map(entry -> {
                    Long userId = entry.getKey();
                    UUID statsId = entry.getValue().getFirst().getStatsId();
                    String firstName = entry.getValue().getFirst().getFirstName();
                    Integer year = entry.getValue().getFirst().getYear();
                    Boolean isWinner = entry.getValue().getFirst().getIsWinner();
                    long totalScore = entry.getValue().stream().mapToLong(Stats::getScore).sum();
                    return new Stats(statsId, chatId, userId, firstName, totalScore, year, isWinner);
                })
                .toList();
    }

    public long getTotalScoreByChatIdAndUserId(Long chatId, Long userId) {
        return statsRepository.findAllByChatId(chatId).stream()
                .filter(stats -> stats.getUserId().equals(userId))
                .mapToLong(Stats::getScore)
                .sum();
    }
}
