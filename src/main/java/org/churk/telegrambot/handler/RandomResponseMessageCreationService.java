package org.churk.telegrambot.handler;

import lombok.RequiredArgsConstructor;
import org.churk.telegrambot.fact.FactMessageCreationService;
import org.churk.telegrambot.sticker.StickerMessageCreationService;
import org.churk.telegrambot.utility.HandlerContext;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class RandomResponseMessageCreationService extends MessageCreationService {
    private final FactMessageCreationService factHandler;
    private final StickerMessageCreationService stickerHandler;
    private final Random random = new Random();

    private boolean shouldTriggerRandomResponse() {
        return random.nextInt(100) < botProperties.getRandomResponseChance() * 10;
    }

    private CommandHandler selectRandomHandler() {
        List<CommandHandler> handlersToChooseFrom = List.of(
                factHandler,
                stickerHandler
        );
        int index = random.nextInt(handlersToChooseFrom.size());
        return handlersToChooseFrom.get(index);
    }

    @Override
    public List<Validable> handle(HandlerContext context) {
        if (!shouldTriggerRandomResponse()) {
            return List.of();
        }
        CommandHandler randomHandler = selectRandomHandler();
        context.setReply(ThreadLocalRandom.current().nextBoolean());
        return randomHandler.handle(context);
    }

    @Override
    public Command getSupportedCommand() {
        return null;
    }
}
