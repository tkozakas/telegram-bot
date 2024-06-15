package org.churk.telegrambot.handler;

import lombok.extern.slf4j.Slf4j;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.service.GroqService;
import org.churk.telegrambot.service.TtsService;
import org.churk.telegrambot.utility.UpdateContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
public class GroqResponseHandler extends ResponseHandler {
    private static final Map<Long, Queue<String>> messageHistory = new ConcurrentHashMap<>();
    private static final Map<Long, Long> lastAccessed = new ConcurrentHashMap<>();
    private static final int MAX_SESSIONS = 1000; // Maximum number of sessions
    private static final long SESSION_TIMEOUT = TimeUnit.HOURS.toMillis(1); // 1 hour timeout

    private final GroqService groqService;
    private final TtsService ttsService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public GroqResponseHandler(GroqService groqService, TtsService ttsService) {
        this.groqService = groqService;
        this.ttsService = ttsService;
        startSessionCleanupTask();
    }

    @Override
    public List<Validable> handle(UpdateContext context) {
        List<String> args = context.getArgs();
        Long chatId = context.getUpdate().getMessage().getChatId();
        String userName = context.getUpdate().getMessage().getFrom().getUserName();

        if (args.isEmpty()) {
            return createReplyMessage(context, "Save a fact using /fact add <fact>");
        }

        String prompt = args.stream()
                .map(String::trim)
                .collect(Collectors.joining(" "))
                .replace("\n", " ")
                .replace("\r", " ");

        Queue<String> chatHistoryQueue = messageHistory.computeIfAbsent(chatId, id -> new LinkedList<>());
        String lastReply = getLastReply(chatHistoryQueue);
        String messageHistoryStr = buildMessageHistory(chatHistoryQueue);

        String reply = groqService.chatWithGroq(prompt, lastReply, messageHistoryStr);
        chatHistoryQueue.add(userName + ": " + prompt);
        chatHistoryQueue.add("bot: " + reply);

        messageHistory.put(chatId, chatHistoryQueue);
        lastAccessed.put(chatId, System.currentTimeMillis());

        return getAudioMessage(context, reply);
    }

    private String getLastReply(Queue<String> chatHistoryQueue) {
        List<String> historyList = new ArrayList<>(chatHistoryQueue);
        for (int i = historyList.size() - 1; i >= 0; i--) {
            if (historyList.get(i).startsWith("bot: ")) {
                return historyList.get(i);
            }
        }
        return "";
    }

    private String buildMessageHistory(Queue<String> chatHistoryQueue) {
        StringBuilder messageHistoryStr = new StringBuilder();
        for (String message : chatHistoryQueue) {
            messageHistoryStr.append(message).append(" ");
        }
        return messageHistoryStr.toString().trim();
    }

    private List<Validable> getAudioMessage(UpdateContext context, String response) {
        Optional<File> audioMessage = ttsService.getSpeech(response);
        if (audioMessage.isPresent()) {
            return createAudioMessage(context, response, audioMessage.get());
        }
        log.error("Failed to generate audio message for: {}", response);
        return createTextMessage(context, response);
    }

    @Override
    public Command getSupportedCommand() {
        return Command.GPT;
    }

    private void startSessionCleanupTask() {
        scheduler.scheduleAtFixedRate(this::clearInactiveSessions, 1, 1, TimeUnit.HOURS);
    }

    private void clearInactiveSessions() {
        long now = System.currentTimeMillis();

        lastAccessed.entrySet().removeIf(entry -> {
            if (now - entry.getValue() > SESSION_TIMEOUT) {
                messageHistory.remove(entry.getKey());
                return true;
            }
            return false;
        });

        while (messageHistory.size() > MAX_SESSIONS) {
            Long oldestSession = Collections.min(lastAccessed.entrySet(), Map.Entry.comparingByValue()).getKey();
            messageHistory.remove(oldestSession);
            lastAccessed.remove(oldestSession);
        }
    }
}
