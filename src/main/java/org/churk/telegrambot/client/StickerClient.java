package org.churk.telegrambot.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "telegramClient", url = "https://api.telegram.org")
public interface StickerClient {

    @GetMapping("/bot{botToken}/getStickerSet")
    Map<String, Object> getStickerSet(@PathVariable("botToken") String botToken, @RequestParam("name") String stickerSetName);
}
