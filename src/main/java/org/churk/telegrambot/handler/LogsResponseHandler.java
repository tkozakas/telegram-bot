package org.churk.telegrambot.handler;

import lombok.RequiredArgsConstructor;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.model.SubCommand;
import org.churk.telegrambot.model.UpdateContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.io.File;
import java.util.List;

import static org.apache.commons.io.file.PathUtils.isEmptyFile;

@Component
@RequiredArgsConstructor
public class LogsResponseHandler extends ResponseHandler {

    @Override
    public List<Validable> handle(UpdateContext context) {
        if (context.getArgs().isEmpty()) {
            return getLogs(context, "logs/app.log");
        }

        SubCommand subCommand = SubCommand.getSubCommand(context.getArgs().getFirst());
        if (subCommand == null) {
            return createReplyMessage(context, "Invalid subcommand");
        }
        return getLogs(context, "logs/app-today.log");
    }

    private List<Validable> getLogs(UpdateContext context, String path) {
        File file = new File(path);
        try {
            if (isEmptyFile(file.toPath()) || !file.exists()) {
                return createReplyMessage(context, "No logs available");
            }
        } catch (Exception e) {
            return createReplyMessage(context, "No logs available");
        }
        return createDocumentMessage(context, file);
    }

    @Override
    public Command getSupportedCommand() {
        return Command.LOGS;
    }
}
