package org.churk.telegrambot.builder;

import org.churk.telegrambot.model.Stat;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StatsListDecorator {
    private final List<Stat> statList;

    public StatsListDecorator(List<Stat> statList) {
        this.statList = statList;
    }

    public String getFormattedStats(String statsTable, String header, String footer, int limit) {
        List<Stat> sortedStats = statList.stream()
                .sorted(Comparator.comparing(Stat::getScore).reversed())
                .limit(limit)
                .toList();

        return IntStream.range(0, sortedStats.size())
                .mapToObj(i -> statsTable.formatted(i + 1, sortedStats.get(i).getFirstName(), sortedStats.get(i).getScore()))
                .collect(Collectors.joining("", header, footer));
    }

}
