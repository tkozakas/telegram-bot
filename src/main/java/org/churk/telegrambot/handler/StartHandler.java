package org.churk.telegrambot.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrambot.builder.MessageBuilderFactory;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.service.ChatService;
import org.churk.telegrambot.service.DailyMessageService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class StartHandler implements CommandHandler {
    private ChatService chatService;
    private DailyMessageService dailyMessageService;
    private MessageBuilderFactory messageBuilderFactory;

    @Override
    public List<Validable> handle(HandlerContext context) {
        chatService.saveChat(context.getUpdate());
        return List.of(messageBuilderFactory.createTextMessageBuilder(context.getUpdate().getMessage().getChatId())
                .withText(dailyMessageService.getKeyNameSentence("welcome_message"))
                .build());
    }

    @Override
    public Command getSupportedCommand() {
        return Command.START;
    }
}
