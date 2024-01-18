package org.churk.telegrampibot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BeanConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public BotConfig botConfig() {
        return new BotConfig();
    }

    @Bean
    public MemeConfig memeConfig() {
        return new MemeConfig();
    }
}
