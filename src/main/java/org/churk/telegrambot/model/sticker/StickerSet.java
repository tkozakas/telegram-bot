package org.churk.telegrambot.model.sticker;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StickerSet {
    private List<Sticker> stickers;
}
