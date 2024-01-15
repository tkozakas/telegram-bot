package org.churk.telegrampibot.config;

import lombok.extern.slf4j.Slf4j;
import org.churk.telegrampibot.service.TelegramBot;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Component
@Slf4j
public class BotInitializer {
    private final TelegramBot telegramBot;

    public BotInitializer(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @EventListener({ContextRefreshedEvent.class})
    public void init() throws TelegramApiException, IOException, InterruptedException {
        stickerInit();

        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            telegramBotsApi.registerBot(telegramBot);
        } catch (TelegramApiException e) {
            log.error("Error while initializing bot", e);
        }
    }

    private static void stickerInit() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("python", "src/main/resources/stickerpack-extract-script.py");
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            log.info(line);
        }
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            log.error("Exited with error code : " + exitCode);
        }
    }
}
