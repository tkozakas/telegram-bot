package org.churk.telegrambot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("loader")
public class LoaderProperties {
    private boolean loadStickers;
    private boolean loadDailyMessages;
    private boolean loadFacts;
}
