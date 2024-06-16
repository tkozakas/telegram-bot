package org.churk.telegrambot.handler;

import lombok.RequiredArgsConstructor;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.utility.UpdateContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LogsResponseHandler extends ResponseHandler {

    @Override
    public List<Validable> handle(UpdateContext context) {
        String todayLogs = "logs/app.%s.log".formatted(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        System.out.println(todayLogs);
        File file = new File("logs/app%s.log");
        return createDocumentMessage(context, file);
    }

    @Override
    public Command getSupportedCommand() {
        return Command.LOGS;
    }
}
