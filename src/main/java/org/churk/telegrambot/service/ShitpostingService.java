package org.churk.telegrambot.service;

import lombok.AllArgsConstructor;
import org.churk.telegrambot.client.ShitpostingClient;
import org.churk.telegrambot.config.DownloadMediaProperties;
import org.churk.telegrambot.model.Quote;
import org.churk.telegrambot.model.Shitpost;
import org.churk.telegrambot.utility.FileDownloader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@AllArgsConstructor
public class ShitpostingService {
    private final ShitpostingClient shitpostingClient;
    private final DownloadMediaProperties downloadMediaProperties;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    public Shitpost getShitpost() {
        return shitpostingClient.getShitpost();
    }

    public Shitpost getShitpostByName(String search) {
        return shitpostingClient.getShitpost(search);
    }

    public Optional<File> getFile(String post) throws ExecutionException, InterruptedException {
        return executorService.submit(() -> {
            String extension = post.substring(post.lastIndexOf("."));
            return FileDownloader.downloadAndCompressMedia(post, downloadMediaProperties, extension);
        }).get();
    }

    public Quote getQuote() {
        return shitpostingClient.getQuote();
    }
}
