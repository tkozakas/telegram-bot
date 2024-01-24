package org.churk.telegrambot.handler;

import lombok.Builder;
import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Data
@Builder
public final class HandlerContext {
    private Update update;
    private List<String> args;
    private boolean isReply;

    public HandlerContext(Update update, List<String> args, boolean isReply) {
        this.update = update;
        this.args = args;
        this.isReply = isReply;
    }
}
