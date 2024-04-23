package org.churk.telegrambot.service;

import lombok.AllArgsConstructor;
import org.churk.telegrambot.client.ShitpostingClient;
import org.churk.telegrambot.config.DownloadMediaProperties;
import org.churk.telegrambot.model.Quote;
import org.churk.telegrambot.utility.FileDownloader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
public class ShitpostingService {
    private final ShitpostingClient shitpostingClient;
    private final DownloadMediaProperties downloadMediaProperties;

    public String getShitpost() {
        Map<String, Object> jsonObject = shitpostingClient.getShitpost();
        return (String) jsonObject.get("url");
    }

    public String getShitpostByName(String search) {
        Map<String, Object> jsonObject = shitpostingClient.getShitpost(search);
        return (String) jsonObject.get("url");
    }

    public CompletableFuture<Optional<File>> getFile(String post) {
        String extension = post.substring(post.lastIndexOf("."));
        return FileDownloader.downloadAndCompressMediaAsync(post, downloadMediaProperties, extension);
    }

    public Quote getQuote() {
        return shitpostingClient.getQuote();
    }
}
