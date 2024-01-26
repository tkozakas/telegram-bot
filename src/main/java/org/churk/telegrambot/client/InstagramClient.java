package org.churk.telegrambot.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@FeignClient(name = "instagramClient", url = "https://www.instagram.com")
public interface InstagramClient {

    // Method to get the JSON response for a particular Instagram post
    @GetMapping("/p/{postCode}/?__a=1")
    @ResponseBody
    Map<String, Object> getPostData(@PathVariable("postCode") String postCode);

    // Method to get the JSON response for a video post
    @ResponseBody
    @GetMapping("/p/{postCode}/?__a=1&__d=dis")
    Map<String, Object> getVideoPostData(@PathVariable("postCode") String postCode);
}
