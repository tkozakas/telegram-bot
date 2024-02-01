package org.churk.telegrambot.stats;

import lombok.RequiredArgsConstructor;
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


    public Optional<Stat> getStatsByChatIdAndUserId(Long chatId, Long userId) {
        return statsRepository.getStatsByChatIdAndUserId(chatId, userId);
    }

    public void registerByUserIdAndChatId(Long userId, Long chatId, String firstName) {
        Stat stat = new Stat();
        stat.setUserId(userId);
        stat.setChatId(chatId);
        stat.setFirstName(firstName);
        stat.setScore(0L);
        stat.setIsWinner(Boolean.FALSE);
        stat.setYear(LocalDateTime.now().getYear());
        statsRepository.save(stat);
    }

    public List<Stat> getStatsByChatIdAndYear(Long chatId, int year) {
        return statsRepository.getStatsByChatIdAndYear(chatId, year);
    }

    public void updateStats(Stat randomWinner) {
        randomWinner.setScore(randomWinner.getScore() + 1);
        randomWinner.setIsWinner(Boolean.TRUE);
        statsRepository.save(randomWinner);
    }

    public List<Stat> getAllStatsByChatIdAndYear(Long chatId, int year) {
        return statsRepository.findAllByChatIdAndYear(chatId, year);
    }

    public List<Stat> getAllStatsByChatId(Long chatId) {
        return statsRepository.findAllByChatId(chatId).stream()
                .collect(Collectors.groupingBy(Stat::getUserId))
                .entrySet().stream()
                .map(entry -> {
                    Long userId = entry.getKey();
                    UUID statsId = entry.getValue().getFirst().getStatsId();
                    String firstName = entry.getValue().getFirst().getFirstName();
                    Integer year = entry.getValue().getFirst().getYear();
                    Boolean isWinner = entry.getValue().getFirst().getIsWinner();
                    Long totalScore = entry.getValue().stream().mapToLong(Stat::getScore).sum();
                    return new Stat(statsId, userId, chatId, firstName, year, totalScore, isWinner);
                })
                .toList();
    }

    public long getTotalScoreByChatIdAndUserId(Long chatId, Long userId) {
        return statsRepository.findAllByChatId(chatId).stream()
                .filter(stats -> stats.getUserId().equals(userId))
                .mapToLong(Stat::getScore)
                .sum();
    }

    public void reset() {
        List<Stat> stats = statsRepository.findAll();
        stats.forEach(stat -> stat.setIsWinner(Boolean.FALSE));
        statsRepository.saveAll(stats);
    }
}
