package org.churk.telegrambot.handler;

import lombok.RequiredArgsConstructor;
import org.churk.telegrambot.builder.ListResponseHandler;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.model.Sticker;
import org.churk.telegrambot.model.SubCommand;
import org.churk.telegrambot.service.StickerService;
import org.churk.telegrambot.model.UpdateContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.UnaryOperator;

@Component
@RequiredArgsConstructor
public class StickerResponseHandler extends ListResponseHandler<String> {
    private final StickerService stickerService;

    @Override
    public List<Validable> handle(UpdateContext context) {
        if (context.getArgs().isEmpty()) {
            return handleGetRandomSticker(context);
        }

        SubCommand subCommand = SubCommand.getSubCommand(context.getArgs().getFirst().toLowerCase());

        if (subCommand == null) {
            return createReplyMessage(context,
                    "Invalid command, please use %s %s"
                            .formatted(Command.STICKER.getPatternCleaned(), Command.STICKER.getSubCommands()));
        }

        return switch (subCommand) {
            case ADD -> handleAdd(context);
            case LIST -> handleList(context);
            case REMOVE -> handleRemove(context);
            default -> handleGetRandomSticker(context);
        };
    }

    private List<Validable> handleGetRandomSticker(UpdateContext context) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        List<Sticker> stickers = stickerService.getStickerSets(chatId);

        if (stickers.isEmpty()) {
            return createReplyMessage(context, "No sticker sets available");
        }
        Sticker randomSticker = stickers.get(ThreadLocalRandom.current().nextInt(stickers.size()));
        return createStickerMessage(context, randomSticker);
    }

    private List<Validable> handleRemove(UpdateContext context) {
        String subCommand = context.getArgs().getLast();
        Long chatId = context.getUpdate().getMessage().getChatId();

        if (subCommand.isEmpty()) {
            return createReplyMessage(context,
                    "Please provide a valid name %s %s <name>"
                            .formatted(Command.STICKER.getPatternCleaned(), SubCommand.REMOVE.getCommand().getFirst()));
        }
        if (!stickerService.existsByChatIdAndStickerName(chatId, subCommand)) {
            return createReplyMessage(context, "Sticker set " + subCommand + " does not exist in the list");
        }
        stickerService.deleteSticker(chatId, subCommand);
        return createReplyMessage(context, "Sticker set " + subCommand + " removed");
    }

    private List<Validable> handleList(UpdateContext context) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        List<String> stickerSets = stickerService.getStickerSetNames(chatId);
        UnaryOperator<String> stickerFormatter = "- *%s*\n"::formatted;
        return formatListResponse(context, stickerSets, stickerFormatter, "Sticker sets:\n", "", "No sticker sets available");
    }

    private List<Validable> handleAdd(UpdateContext context) {
        String subCommand = context.getArgs().getLast();
        Long chatId = context.getUpdate().getMessage().getChatId();

        if (subCommand.isEmpty() || !stickerService.isValidSticker(subCommand)) {
            return createReplyMessage(context,
                    "Please provide a valid name %s %s <name>"
                            .formatted(Command.STICKER.getPatternCleaned(), SubCommand.ADD.getCommand().getFirst()));
        }
        if (stickerService.existsByChatIdAndStickerName(chatId, subCommand)) {
            return createReplyMessage(context, "Sticker set " + subCommand + " already exists in the list");
        }
        stickerService.addSticker(chatId, subCommand);
        return createReplyMessage(context, "Sticker set " + subCommand + " added");
    }

    @Override
    public Command getSupportedCommand() {
        return Command.STICKER;
    }
}
