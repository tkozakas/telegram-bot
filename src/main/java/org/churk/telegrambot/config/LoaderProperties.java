package org.churk.telegrambot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("loader")
public class LoaderProperties {
    private String stickerSetsPath;
    private boolean loadStickers;

    private String subredditsPath;
    private boolean loadSubreddits;

    private String dailyMessagesPath;
    private boolean loadDailyMessages;

    private String factsPath;
    private boolean loadFacts;
}
