package org.churk.telegrambot.decorator;

import org.churk.telegrambot.model.Stat;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class StatsListDecorator {
    private final List<Stat> statList;

    public StatsListDecorator(List<Stat> statList) {
        this.statList = statList;
    }

    public String getFormattedStats(String statsTable, String header, String footer) {
        return statList.stream()
                .sorted(Comparator.comparing(Stat::getScore).reversed())
                .limit(10)
                .map(stat -> statsTable.formatted(statList.indexOf(stat) + 1, stat.getFirstName(), stat.getScore()))
                .collect(Collectors.joining("", header, footer));
    }
}
