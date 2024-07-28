package org.churk.telegrambot.handler;

import lombok.AllArgsConstructor;
import org.churk.telegrambot.client.MemeClient;
import org.churk.telegrambot.model.*;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.util.List;

@Component
@AllArgsConstructor
public class ShitpostingResponseHandler extends ResponseHandler {
    private final MemeClient memeClient;

    @Override
    public List<Validable> handle(UpdateContext context) {
        if (context.getArgs().isEmpty()) {
            Shitpost post = memeClient.getShitPost().getBody();
            return post.isError() ?
                    createReplyMessage(context, "Not found") :
                    handlePost(context, post.getUrl(), "Random Shitpost");
        }

        SubCommand subCommand = SubCommand.getSubCommand(context.getArgs().getFirst().toLowerCase());
        return switch (subCommand) {
            case QUOTE -> handleQuote(context);
            default -> {
                Shitpost post = memeClient.getShitPost(context.getArgs().getFirst()).getBody();
                yield post.isError() ?
                        createReplyMessage(context, "Not found") :
                        handlePost(context, post.getUrl(), "Shitpost: " + context.getArgs().getFirst());
            }
        };
    }

    private List<Validable> handleQuote(UpdateContext context) {
        Quote quote = memeClient.getShitPostQuote().getBody();
        String message = quote.getQuote() + "\n\n" + quote.getQuotee();
        return createTextMessage(context, message);
    }

    private List<Validable> handlePost(UpdateContext context, String url, String caption) {
        return createVideoMessage(context, url, caption);
    }

    @Override
    public Command getSupportedCommand() {
        return Command.SHITPOST;
    }
}
