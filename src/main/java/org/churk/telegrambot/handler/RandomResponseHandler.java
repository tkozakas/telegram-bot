package org.churk.telegrambot.handler;

import lombok.AllArgsConstructor;
import org.churk.telegrambot.config.BotProperties;
import org.churk.telegrambot.model.Command;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.util.List;
import java.util.Random;

@Service
@AllArgsConstructor
public class RandomResponseHandler implements CommandHandler {
    private final BotProperties botProperties;
    private final FactHandler factHandler;
    private final StickerHandler stickerHandler;
    private final Random random = new Random();

    private boolean shouldTriggerRandomResponse() {
        return random.nextInt(100) < botProperties.getRandomResponseChance() * 100;
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
        context.setReply(true);
        return randomHandler.handle(context);
    }

    @Override
    public Command getSupportedCommand() {
        return null;
    }
}
