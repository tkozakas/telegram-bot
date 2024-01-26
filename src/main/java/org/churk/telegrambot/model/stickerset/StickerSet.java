package org.churk.telegrambot.model.stickerset;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.churk.telegrambot.model.bot.Sticker;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StickerSet {
    private List<Sticker> stickers;
}
