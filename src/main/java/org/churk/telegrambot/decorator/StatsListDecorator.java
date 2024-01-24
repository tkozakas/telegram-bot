package org.churk.telegrambot.decorator;

import org.churk.telegrambot.model.Stats;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StatsListDecorator {
    private final List<Stats> statsList;

    public StatsListDecorator(List<Stats> statsList) {
        this.statsList = statsList;
    }

    public String getFormattedStats(String statsTable, String header, String footer) {
        return IntStream
                .iterate(0, i -> i < statsList.size() && i < 10, i -> i + 1)
                .mapToObj(i -> statsTable.
                        formatted(i + 1, statsList.get(i).getFirstName(), statsList.get(i).getScore()))
                .collect(Collectors.joining("", header, footer));
    }
}
