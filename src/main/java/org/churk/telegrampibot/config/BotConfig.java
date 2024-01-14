package org.churk.telegrampibot.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class BotConfig {
    @Value("${bot.username}")
    private String username;
    @Value("${bot.token}")
    private String token;
}
