package org.churk.telegrampibot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrampibot.model.Fact;
import org.churk.telegrampibot.repository.FactRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class FactService {
    private final FactRepository factRepository;
    public String getRandomFact() {
        List<Fact> factList = factRepository.findAll();
        int randomIndex = ThreadLocalRandom.current().nextInt(0, factList.size());
        Fact fact = factList.get(randomIndex);
        return fact.getComment();
    }
}
