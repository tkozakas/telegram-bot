package org.churk.telegrambot.builder;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ListDecorator<T> {
    private final List<T> items;
    private final Function<T, String> itemNameExtractor;

    public ListDecorator(List<T> items, Function<T, String> itemNameExtractor) {
        this.items = items;
        this.itemNameExtractor = itemNameExtractor;
    }

    public String getFormattedList(String header, String footer, int limit, boolean escapeMarkdown) {
        String formattedItems = items.stream()
                .limit(limit)
                .map(itemNameExtractor)
                .map(name -> !escapeMarkdown ? escapeMarkdown(name) : name)
                .collect(Collectors.joining());

        return header + formattedItems + footer;
    }

    private String escapeMarkdown(String text) {
        return text.replaceAll("([_\\\\*\\[\\]()~`>#+\\-=|{}.!])", "\\\\$1");
    }
}
