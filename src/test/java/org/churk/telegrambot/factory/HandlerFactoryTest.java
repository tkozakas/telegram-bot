package org.churk.telegrambot.factory;

import org.churk.telegrambot.handler.CommandHandler;
import org.churk.telegrambot.handler.HandlerFactory;
import org.churk.telegrambot.handler.RandomResponseHandler;
import org.churk.telegrambot.handler.StartHandler;
import org.churk.telegrambot.model.Command;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HandlerFactoryTest {
    @Mock
    private RandomResponseHandler randomResponseHandler;
    @InjectMocks
    private StartHandler startHandler;
    private HandlerFactory handlerFactory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handlerFactory = new HandlerFactory(List.of(startHandler), randomResponseHandler);
    }

    @Test
    void getStartHandlerTest() {
        CommandHandler handler = handlerFactory.getHandler(Command.START);
        assertNotNull(handler, "Handler should not be null");
        assertEquals(StartHandler.class, handler.getClass(), "Handler should be of type StartHandler");
    }

    @Test
    void getUnsupportedCommandHandlerTest() {
        CommandHandler handler = handlerFactory.getHandler(Command.NONE);
        assertEquals(randomResponseHandler, handler, "Handler should be of type RandomResponseHandler");
    }
}
