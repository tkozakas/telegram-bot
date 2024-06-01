package org.churk.telegrambot.handler;

import lombok.AllArgsConstructor;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.model.Quote;
import org.churk.telegrambot.model.Shitpost;
import org.churk.telegrambot.model.SubCommand;
import org.churk.telegrambot.service.ShitpostingService;
import org.churk.telegrambot.utility.UpdateContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class ShitpostingHandler extends Handler {
    private final ShitpostingService shitpostingService;

    @Override
    public List<Validable> handle(UpdateContext context) {
        if (context.getArgs().isEmpty()) {
            Shitpost post = shitpostingService.getShitpost();
            return post.isError() ?
                    getReplyMessage(context.getUpdate().getMessage().getChatId(), context.getUpdate().getMessage().getMessageId(), "Not found") :
                    handlePost(context, post.getUrl(), "Random Shitpost");
        }

        SubCommand subCommand = SubCommand.getSubCommand(context.getArgs().getFirst().toLowerCase());
        return switch (subCommand) {
            case QUOTE -> handleQuote(context);
            default -> {
                Shitpost post = shitpostingService.getShitpostByName(context.getArgs().getFirst());
                yield post.isError() ?
                        getReplyMessage(context.getUpdate().getMessage().getChatId(), context.getUpdate().getMessage().getMessageId(), "Not found") :
                        handlePost(context, post.getUrl(), "Shitpost: " + context.getArgs().getFirst());
            }
        };
    }

    private List<Validable> handleQuote(UpdateContext context) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();

        Quote quote = shitpostingService.getQuote();
        String message = quote.getQuote() + "\n\n" + quote.getQuotee();
        return getReplyMessage(chatId, messageId, message);
    }

    private List<Validable> handlePost(UpdateContext context, String url, String caption) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();

        try {
            Optional<File> file = shitpostingService.getFile(url);
            if (file.isEmpty()) {
                return getReplyMessage(chatId, messageId, "Error downloading file");
            }
            file.get().deleteOnExit();
            return getVideo(chatId, file.get(), caption);

        } catch (Exception e) {
            return getReplyMessage(chatId, messageId, "Error processing file");
        }
    }

    @Override
    public Command getSupportedCommand() {
        return Command.SHITPOST;
    }
}
