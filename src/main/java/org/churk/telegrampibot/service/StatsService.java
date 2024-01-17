package org.churk.telegrampibot.service;

import org.churk.telegrampibot.model.Stats;
import org.churk.telegrampibot.repository.StatsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StatsService {
    private final StatsRepository statsRepository;

    public StatsService(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    List<Stats> getAggregatedStatsByChatId(Long chatId) {
        return statsRepository.findAll().stream()
                .filter(stats -> stats.getChatId().equals(chatId))
                .collect(Collectors.groupingBy(Stats::getUserId))
                .entrySet().stream()
                .map(entry -> {
                    Long userId = entry.getKey();
                    UUID statsId = entry.getValue().get(0).getStatsId();
                    String firstName = entry.getValue().get(0).getFirstName();
                    LocalDateTime createdAt = entry.getValue().get(0).getCreatedAt();
                    Boolean isWinner = entry.getValue().get(0).getIsWinner();
                    long totalScore = entry.getValue().stream().mapToLong(Stats::getScore).sum();
                    return new Stats(statsId, chatId, userId, firstName, totalScore, createdAt, isWinner);
                })
                .toList();
    }

    public List<Stats> getStatsByChatIdAndYear(Long chatId, int year) {
        return statsRepository.findAll().stream()
                .filter(stats -> stats.getChatId().equals(chatId) && stats.getCreatedAt().getYear() == year)
                .toList();
    }

    public List<Stats> getStatsByChatIdAndUserId(Long chatId, Long userId) {
        return statsRepository.findAll().stream()
                .filter(stats -> stats.getChatId().equals(chatId) && stats.getUserId().equals(userId))
                .toList();
    }

    public boolean existsByUserId(Long userId) {
        return statsRepository.findAll().stream()
                .anyMatch(stats -> stats.getUserId().equals(userId));
    }

    public boolean existsWinnerToday() {
        return statsRepository.findAll().stream()
                .anyMatch(stats -> stats.getIsWinner() == Boolean.TRUE);
    }

    public List<Stats> getAllStats() {
        return statsRepository.findAll();
    }

    public void addStat(Stats stats) {
        statsRepository.save(stats);
    }

    public void updateStats(Stats stats) {
        statsRepository.save(stats);
    }

    public void updateStats(List<Stats> stats) {
        statsRepository.saveAll(stats);
    }
}
