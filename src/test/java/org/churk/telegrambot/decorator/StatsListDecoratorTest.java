package org.churk.telegrambot.decorator;

import org.churk.telegrambot.model.bot.Stat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class StatsListDecoratorTest {
    @Test
    void getFormattedStatsTest() {
        List<Stat> statList = List.of(new Stat("Alice", 9L),
                new Stat("Bob", 7L),
                new Stat("Charlie", 8L));

        StatsListDecorator statsListDecorator = new StatsListDecorator(statList);
        String result = statsListDecorator.getFormattedStats("%d %s %d", "header", "footer");

        String expected = "header" +
                "1 Alice 9" +
                "2 Charlie 8" +
                "3 Bob 7" +
                "footer";
        assertEquals(expected, result);
    }
}
