package org.churk.telegrambot.service;

import lombok.RequiredArgsConstructor;
import org.churk.telegrambot.model.Stat;
import org.churk.telegrambot.repository.StatsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsService {
    private final StatsRepository statsRepository;

    public List<Stat> getStatsByChatIdAndUserId(Long chatId, Long userId) {
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

    public List<Stat> getAllStatsByChatIdAndYear(Long chatId, int year) {
        return statsRepository.findAllByChatIdAndYear(chatId, year);
    }

    public List<Stat> getAllStatsByChatId(Long chatId) {
        Map<Long, List<Stat>> groupedStats = statsRepository.findAllByChatId(chatId)
                .stream()
                .collect(Collectors.groupingBy(Stat::getUserId));

        return groupedStats.values().stream()
                .map(stats -> {
                    Stat firstStat = stats.getFirst();
                    Long totalScore = stats.stream().mapToLong(Stat::getScore).sum();
                    return new Stat(firstStat.getStatsId(), firstStat.getUserId(), chatId, firstStat.getFirstName(), firstStat.getYear(), totalScore, firstStat.getIsWinner());
                })
                .collect(Collectors.toList());
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

    public List<Stat> getUserIdByChatIdAndFirstName(Long chatId, String firstName) {
        return statsRepository.getUserIdByChatIdAndFirstName(chatId, firstName);
    }
}
