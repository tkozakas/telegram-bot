package org.churk.telegrambot.handler;

import lombok.RequiredArgsConstructor;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.service.StickerService;
import org.churk.telegrambot.model.sticker.Sticker;
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
        List<String> args = context.getArgs();
        String subCommand = args.isEmpty() ? "get" : args.getFirst().toLowerCase();

        return switch (subCommand) {
            case "add" -> handleAdd(context);
            case "list" -> handleList(context);
            case "remove" -> handleRemove(context);
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
        List<String> args = context.getArgs().subList(1, context.getArgs().size());
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();

        if (args.isEmpty() || !stickerService.isValidSticker(args.getFirst())) {
            return getReplyMessage(chatId, messageId,
                    "Please provide a valid name /sticker remove <name>");
        }
        if (!stickerService.existsByChatIdAndStickerName(chatId, args.getFirst())) {
            return getReplyMessage(chatId, messageId,
                    "Sticker set " + args.getFirst() + " does not exist in the list");
        }
        stickerService.deleteSticker(chatId, args.getFirst());
        return getReplyMessage(chatId, messageId,
                "Sticker set " + args.getFirst() + " removed");
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
        List<String> args = context.getArgs().subList(1, context.getArgs().size());
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();

        if (args.isEmpty() || !stickerService.isValidSticker(args.getFirst())) {
            return getReplyMessage(chatId, messageId,
                    "Please provide a valid name /sticker add <name>");
        }
        if (stickerService.existsByChatIdAndStickerName(chatId, args.getFirst())) {
            return getReplyMessage(chatId, messageId,
                    "Sticker set " + args.getFirst() + " already exists in the list");
        }
        stickerService.addSticker(chatId, args.getFirst());
        return getReplyMessage(chatId, messageId,
                "Sticker set " + args.getFirst() + " added");
    }

    @Override
    public Command getSupportedCommand() {
        return Command.STICKER;
    }
}
