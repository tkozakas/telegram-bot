package org.churk.telegrambot.instagram;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class InstagramMedia {

    private String id;

    @JsonProperty("media_type")
    private String mediaType;

    @JsonProperty("media_url")
    private String mediaUrl;

    private String username;

    private Date timestamp;
}
