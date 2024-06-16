package org.churk.telegrambot.handler;

import lombok.AllArgsConstructor;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.model.Quote;
import org.churk.telegrambot.model.Shitpost;
import org.churk.telegrambot.model.SubCommand;
import org.churk.telegrambot.service.ShitpostingService;
import org.churk.telegrambot.model.UpdateContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class ShitpostingResponseHandler extends ResponseHandler {
    private final ShitpostingService shitpostingService;

    @Override
    public List<Validable> handle(UpdateContext context) {
        if (context.getArgs().isEmpty()) {
            Shitpost post = shitpostingService.getShitpost();
            return post.isError() ?
                    createReplyMessage(context, "Not found") :
                    handlePost(context, post.getUrl(), "Random Shitpost");
        }

        SubCommand subCommand = SubCommand.getSubCommand(context.getArgs().getFirst().toLowerCase());
        return switch (subCommand) {
            case QUOTE -> handleQuote(context);
            default -> {
                Shitpost post = shitpostingService.getShitpostByName(context.getArgs().getFirst());
                yield post.isError() ?
                        createReplyMessage(context, "Not found") :
                        handlePost(context, post.getUrl(), "Shitpost: " + context.getArgs().getFirst());
            }
        };
    }

    private List<Validable> handleQuote(UpdateContext context) {
        Quote quote = shitpostingService.getQuote();
        String message = quote.getQuote() + "\n\n" + quote.getQuotee();
        return createTextMessage(context, message);
    }

    private List<Validable> handlePost(UpdateContext context, String url, String caption) {
        try {
            Optional<File> file = shitpostingService.getFile(url);
            if (file.isEmpty()) {
                return createReplyMessage(context, "Error downloading file");
            }
            file.get().deleteOnExit();
            return createVideoMessage(context, file.get(), caption);

        } catch (Exception e) {
            return createReplyMessage(context, "Error processing file");
        }
    }

    @Override
    public Command getSupportedCommand() {
        return Command.SHITPOST;
    }
}
