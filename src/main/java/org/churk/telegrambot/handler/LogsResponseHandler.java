package org.churk.telegrambot.handler;

import lombok.RequiredArgsConstructor;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.model.UpdateContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.io.File;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LogsResponseHandler extends ResponseHandler {

    @Override
    public List<Validable> handle(UpdateContext context) {
        File file = new File("logs/app.log");
        return createDocumentMessage(context, file);
    }

    @Override
    public Command getSupportedCommand() {
        return Command.LOGS;
    }
}
