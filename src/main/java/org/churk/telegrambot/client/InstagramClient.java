package org.churk.telegrambot.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "instagramClient", url = "https://www.instagram.com")
public interface InstagramClient {

    // Method to get the JSON response for a particular Instagram post
    @GetMapping("/p/{postCode}/?__a=1")
    String getPostData(@PathVariable("postCode") String postCode);

    // Method to get the JSON response for a video post
    @GetMapping("/p/{postCode}/?__a=1&__d=dis")
    String getVideoPostData(@PathVariable("postCode") String postCode);
}
