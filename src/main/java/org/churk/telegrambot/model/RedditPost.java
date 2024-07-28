package org.churk.telegrambot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RedditPost {
    private String title;
    private String url;
    private String author;

    public boolean isVideo() {
        return url.endsWith(".mp4");
    }

    public boolean isImage() {
        return url.endsWith(".jpg") || url.endsWith(".png") || url.endsWith(".jpeg");
    }

    public boolean isGif() {
        return url.endsWith(".gif");
    }
}
