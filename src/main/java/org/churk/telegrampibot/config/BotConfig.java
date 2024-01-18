package org.churk.telegrampibot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties("bot")
public class BotConfig {
    private String username;
    private String token;
    private String winnerName;
    private String schedule;
    private String resetSchedule;
    private List<String> stickerSets;
}
