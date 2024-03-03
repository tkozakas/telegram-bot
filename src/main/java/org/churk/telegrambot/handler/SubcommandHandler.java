package org.churk.telegrambot.handler;

import org.churk.telegrambot.utility.HandlerContext;

import java.util.List;

public interface SubcommandHandler {
    void handleSubcommand(HandlerContext context, List<String> args);
}
