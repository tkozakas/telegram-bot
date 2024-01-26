package org.churk.telegrambot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrambot.client.InstagramClient;
import org.churk.telegrambot.config.DownloadMediaProperties;
import org.churk.telegrambot.utility.FileDownloader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstagramService {
    private final DownloadMediaProperties instagramProperties;
    private final InstagramClient instagramClient;

    public Optional<File> getInstagramMedia(String postCode) throws feign.FeignException.NotFound {
        Map<String, Object> map = instagramClient.getVideoPostData(postCode);
        return getFile(map);
    }

    private Optional<File> getFile(Map<String, Object> map) {
        Map<String, Object> graphql = (Map<String, Object>) map.get("graphql");
        Map<String, Object> shortcodeMedia = (Map<String, Object>) graphql.get("shortcode_media");
        if (shortcodeMedia.containsKey("is_video") && Boolean.TRUE.equals(shortcodeMedia.get("is_video")) &&
                (shortcodeMedia.containsKey("video_url") && shortcodeMedia.get("video_url") instanceof String)) {

            String apiUrl = (String) shortcodeMedia.get("video_url");
            String extension = ".mp4";
            return FileDownloader.downloadAndCompressMedia(apiUrl, instagramProperties, extension);
        }
        return Optional.empty();
    }
}
