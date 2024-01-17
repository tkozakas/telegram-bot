package org.churk.telegrampibot.repository;

import org.churk.telegrampibot.model.Sticker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StickerRepository extends JpaRepository<Sticker, Long> {
}
