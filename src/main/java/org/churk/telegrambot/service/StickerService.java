package org.churk.telegrambot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrambot.model.Sticker;
import org.churk.telegrambot.repository.StickerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


@Slf4j
@Service
@RequiredArgsConstructor
public class StickerService {
    private final StickerRepository stickerRepository;

    public String getRandomStickerId() {
        List<Sticker> stickers = stickerRepository.findAll();
        int randomIndex = ThreadLocalRandom.current().nextInt(stickers.size());
        return stickers.get(randomIndex).getFileId();
    }
}
