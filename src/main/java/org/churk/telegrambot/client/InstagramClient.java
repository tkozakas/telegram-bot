package org.churk.telegrambot.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "instagramClient", url = "https://graph.instagram.com")
public interface InstagramClient {

    @GetMapping("/{mediaId}")
    String getMediaData(@PathVariable("mediaId") String mediaId,
                        @RequestParam("fields") String fields,
                        @RequestParam("access_token") String accessToken);
}


