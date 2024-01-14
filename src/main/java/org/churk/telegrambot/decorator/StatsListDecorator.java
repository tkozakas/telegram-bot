package org.churk.telegrambot.decorator;

import org.churk.telegrambot.model.Stat;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StatsListDecorator {
    private final List<Stat> statList;

    public StatsListDecorator(List<Stat> statList) {
        this.statList = statList;
    }

    public String getFormattedStats(String statsTable, String header, String footer) {
        return IntStream
                .iterate(0, i -> i < statList.size() && i < 10, i -> i + 1)
                .mapToObj(i -> statsTable.
                        formatted(i + 1, statList.get(i).getFirstName(), statList.get(i).getScore()))
                .collect(Collectors.joining("", header, footer));
    }
}
