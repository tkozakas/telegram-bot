package org.churk.telegrambot.handler;

import lombok.RequiredArgsConstructor;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.utility.UpdateContext;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class RandomResponseHandler extends Handler {
    private final FactHandler factHandler;
    private final StickerHandler stickerHandler;
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
    public List<Validable> handle(UpdateContext context) {
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
