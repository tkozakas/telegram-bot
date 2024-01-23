package org.churk.telegrambot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrambot.model.Fact;
import org.churk.telegrambot.repository.FactRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FactService {
    private final FactRepository factRepository;

    public List<Fact> getAllFacts() {
        return factRepository.findAll();
    }
}
