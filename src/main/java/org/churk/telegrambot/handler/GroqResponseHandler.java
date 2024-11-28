package org.churk.telegrambot.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrambot.client.MemeClient;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.model.GptRequest;
import org.churk.telegrambot.model.UpdateContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class GroqResponseHandler extends ResponseHandler {
    private final MemeClient memeClient;

    @Override
    public List<Validable> handle(UpdateContext context) {
        Long chatId = context.getChatId();
        List<String> args = context.getArgs();
        if (args.isEmpty()) {
            return createReplyMessage(context, "Please provide a prompt");
        }
        if (args.getFirst().contains("clear")) {
            String reply = memeClient.clearMemory(chatId).getBody();
            return createReplyMessage(context, reply);
        }
        if (args.getFirst().contains("memory")) {
            File file = new File("memory.txt");
            String reply = memeClient.getMemory(chatId);
            return writeToFile(context, file, reply);
        }
        GptRequest gptRequest = new GptRequest();
        gptRequest.setPrompt(String.join(" ", args));
        gptRequest.setChatId(chatId);
        gptRequest.setUsername(context.getFirstName());
        String reply = memeClient.getGpt(gptRequest).getBody();
        return createTextMessage(context, reply);
    }

    private List<Validable> writeToFile(UpdateContext context, File file, String reply) {
        try {
            Files.writeString(file.toPath(), reply);
            return createDocumentMessage(context, file);
        } catch (IOException e) {
            return createReplyMessage(context, "Error while writing to file");
        }
    }

    @Override
    public Command getSupportedCommand() {
        return Command.GPT;
    }
}
