package org.churk.telegrambot.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrambot.client.MemeClient;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.model.GptRequest;
import org.churk.telegrambot.model.UpdateContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

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
        GptRequest gptRequest = new GptRequest();
        gptRequest.setPrompt(String.join(" ", args));
        gptRequest.setChatId(chatId);
        String reply = memeClient.getGpt(gptRequest).getBody();
        return createTextMessage(context, reply);
    }

    @Override
    public Command getSupportedCommand() {
        return Command.GPT;
    }
}
