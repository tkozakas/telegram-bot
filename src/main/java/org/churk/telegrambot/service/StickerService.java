package org.churk.telegrambot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrambot.model.Sticker;
import org.churk.telegrambot.repository.StickerRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class StickerService {
    private final StickerRepository stickerRepository;

    public List<Sticker> getAllStickers() {
        return stickerRepository.findAll();
    }
}
