package org.churk.telegrambot.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "telegramClient", url = "https://api.telegram.org")
public interface TelegramClient {

    @GetMapping("/bot{botToken}/getStickerSet")
    String getStickerSet(@PathVariable("botToken") String botToken, @RequestParam("name") String stickerSetName);
}
