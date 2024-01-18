package org.churk.telegrambot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrambot.model.Fact;
import org.churk.telegrambot.repository.FactRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class FactService {
    private final FactRepository factRepository;
    public String getRandomFact() {
        if (factRepository.count() == 0) {
            log.warn("No facts found in database");
            return null;
        }
        List<Fact> factList = factRepository.findAll();
        int randomIndex = ThreadLocalRandom.current().nextInt(0, factList.size());
        Fact fact = factList.get(randomIndex);
        return fact.getComment();
    }
}
