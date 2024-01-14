package org.churk.telegrampibot.service;

import org.churk.telegrampibot.model.Stats;
import org.churk.telegrampibot.repository.StatsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatsService {
    private final StatsRepository statsRepository;

    public StatsService(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    public List<Stats> getStatsByChatId(Long chatId) {
        return statsRepository.findAll().stream()
                .filter(stats -> stats.getChatId().equals(chatId))
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

    public boolean existsById(Long id) {
        return statsRepository.existsById(id);
    }

    public void addStat(Stats stats) {
        statsRepository.save(stats);
    }

    public List<Stats> getAllStats() {
        return statsRepository.findAll();
    }

    public void updateStats(Stats winner) {
        statsRepository.save(winner);
    }
}
