package org.churk.telegrambot.instagram;

import lombok.Data;

import java.util.Date;

@Data
public class InstagramMedia {
    private String id;
    private String mediaType;
    private String mediaUrl;
    private String username;
    private Date timestamp;
}
