package org.churk.telegrambot.handler;

import lombok.RequiredArgsConstructor;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.model.Sticker;
import org.churk.telegrambot.model.SubCommand;
import org.churk.telegrambot.service.StickerService;
import org.churk.telegrambot.utility.HandlerContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.UnaryOperator;

@Component
@RequiredArgsConstructor
public class StickerHandler extends Handler {
    private final StickerService stickerService;

    @Override
    public List<Validable> handle(HandlerContext context) {
        if (context.getArgs().isEmpty()) {
            return handleGetRandomSticker(context);
        }

        SubCommand subCommand = SubCommand.getSubCommand(context.getArgs().getFirst().toLowerCase());

        if (subCommand == null) {
            return getReplyMessage(context.getUpdate().getMessage().getChatId(),
                    context.getUpdate().getMessage().getMessageId(),
                    "Invalid command, please use %s %s".formatted(Command.STICKER.getPatternCleaned(), Command.STICKER.getSubCommands()));
        }

        return switch (subCommand) {
            case ADD -> handleAdd(context);
            case LIST -> handleList(context);
            case REMOVE -> handleRemove(context);
            default -> handleGetRandomSticker(context);
        };
    }

    private List<Validable> handleGetRandomSticker(HandlerContext context) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        List<Sticker> stickers = stickerService.getStickerSets(chatId);
        Integer messageId = context.getUpdate().getMessage().getMessageId();

        if (stickers.isEmpty()) {
            return getReplyMessage(chatId, messageId,
                    "No sticker sets available");
        }
        Sticker randomSticker = stickers.get(ThreadLocalRandom.current().nextInt(stickers.size()));
        return context.isReply() ?
                getReplySticker(chatId, messageId, randomSticker) :
                getSticker(chatId, randomSticker);
    }

    private List<Validable> handleRemove(HandlerContext context) {
        String subCommand = context.getArgs().getLast();
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();

        if (subCommand.isEmpty()) {
            return getReplyMessage(chatId, messageId,
                    "Please provide a valid name %s %s <name>".formatted(Command.STICKER.getPatternCleaned(), SubCommand.REMOVE.getCommand().getFirst()));
        }
        if (!stickerService.existsByChatIdAndStickerName(chatId, subCommand)) {
            return getReplyMessage(chatId, messageId,
                    "Sticker set " + subCommand + " does not exist in the list");
        }
        stickerService.deleteSticker(chatId, subCommand);
        return getReplyMessage(chatId, messageId,
                "Sticker set " + subCommand + " removed");
    }

    private List<Validable> handleList(HandlerContext context) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();
        List<String> stickerSets = stickerService.getStickerSetNames(chatId);

        UnaryOperator<String> escapeMarkdown = name -> name
                .replaceAll("([_\\\\*\\[\\]()~`>#+\\-=|{}.!])", "\\\\$1");

        String message = "*Sticker sets:*\n" + stickerSets.stream()
                .limit(20)
                .map(escapeMarkdown)
                .reduce("", (a, b) -> a + "- " + b + "\n");
        return stickerSets.isEmpty() ?
                getReplyMessage(chatId, messageId, "No sticker sets available") :
                getMessageWithMarkdown(chatId, message);
    }

    private List<Validable> handleAdd(HandlerContext context) {
        String subCommand = context.getArgs().getLast();
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();

        if (subCommand.isEmpty() || !stickerService.isValidSticker(subCommand)) {
            return getReplyMessage(chatId, messageId,
                    "Please provide a valid name %s %s <name>".formatted(Command.STICKER.getPatternCleaned(), SubCommand.ADD.getCommand().getFirst()));
        }
        if (stickerService.existsByChatIdAndStickerName(chatId, subCommand)) {
            return getReplyMessage(chatId, messageId,
                    "Sticker set " + subCommand + " already exists in the list");
        }
        stickerService.addSticker(chatId, subCommand);
        return getReplyMessage(chatId, messageId,
                "Sticker set " + subCommand + " added");
    }

    @Override
    public Command getSupportedCommand() {
        return Command.STICKER;
    }
}
