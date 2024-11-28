package org.churk.telegrambot.model;

import lombok.Data;

@Data
public class GptRequest {
    private String prompt;
    private Long chatId;
    private String username;
}
