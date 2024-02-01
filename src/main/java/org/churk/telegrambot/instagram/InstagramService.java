package org.churk.telegrambot.instagram;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrambot.config.DownloadMediaProperties;
import org.churk.telegrambot.utility.FileDownloader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstagramService {
    private final DownloadMediaProperties downloadMediaProperties;
    private final InstagramClient instagramClient;

    public Optional<File> getInstagramMedia(String mediaId) {
        try {
            String accessToken = downloadMediaProperties.getAccessToken();
            String fields = "id,media_type,media_url,username,timestamp";
            String jsonResponse = instagramClient.getMediaData(mediaId, fields, accessToken);

            ObjectMapper mapper = new ObjectMapper();
            InstagramMedia instagramMedia = mapper.readValue(jsonResponse, InstagramMedia.class);

            return getFile(instagramMedia).join();
        } catch (JsonProcessingException e) {
            log.error("Error while parsing Instagram response", e);
        } catch (FeignException e) {
            log.error("Error with Feign client", e);
        } catch (Exception e) {
            log.error("Error while getting Instagram media", e);
        }
        return Optional.empty();
    }

    private CompletableFuture<Optional<File>> getFile(InstagramMedia instagramMedia) {
        String mediaUrl = instagramMedia.getMediaUrl();
        String extension = instagramMedia.getMediaType().equals("VIDEO") ? ".mp4" : ".jpg";
        return FileDownloader.downloadAndCompressMediaAsync(mediaUrl, downloadMediaProperties, extension);
    }
}

