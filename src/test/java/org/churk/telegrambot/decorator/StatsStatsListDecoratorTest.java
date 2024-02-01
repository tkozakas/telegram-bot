package org.churk.telegrambot.decorator;

import org.churk.telegrambot.builder.StatsListDecorator;
import org.churk.telegrambot.stats.Stat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class StatsStatsListDecoratorTest {
    @Test
    void getFormattedStatsTest() {
        List<Stat> statList = List.of(new Stat("Alice", 9L),
                new Stat("Bob", 7L),
                new Stat("Charlie", 8L));

        StatsListDecorator listDecorator = new StatsListDecorator(statList);
        String result = listDecorator.getFormattedStats("%d %s %d", "header", "footer", 10);

        String expected = "header" +
                "1 Alice 9" +
                "2 Charlie 8" +
                "3 Bob 7" +
                "footer";
        assertEquals(expected, result);
    }
}
