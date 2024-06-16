package org.churk.telegrambot.handler;

import org.churk.telegrambot.model.Command;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.churk.telegrambot.model.Command.NONE;

@Service
public class HandlerFactory {
    private final Map<Command, CommandHandler> handlerMap = new EnumMap<>(Command.class);

    public HandlerFactory(List<CommandHandler> handlers) {
        handlers.stream()
                .filter(handler -> handler.getSupportedCommand() != null)
                .forEachOrdered(handler -> handlerMap.put(handler.getSupportedCommand(), handler));
    }

    public CommandHandler getHandler(Command command) {
        return command == NONE ? handlerMap.get(Command.RANDOM) : handlerMap.get(command);
    }
}
