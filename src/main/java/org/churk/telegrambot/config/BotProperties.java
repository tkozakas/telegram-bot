package org.churk.telegrambot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties("bot")
public class BotProperties {
    private String username;
    private String token;
    private String winnerName;
    private Double randomResponseChance;
    private List<String> stickerSetNames;
    private List<String> subredditNames;
}
