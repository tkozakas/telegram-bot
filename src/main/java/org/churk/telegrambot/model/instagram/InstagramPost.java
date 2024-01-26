package org.churk.telegrambot.model.instagram;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InstagramPost {
    private Graphql graphql;
}
