package com.kuantik.validation.infrastructure.blacklist.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ArticlesStats(
        @JsonProperty("articulo_69") ArticleStats article69,
        @JsonProperty("articulo_69b") ArticleStats article69b,
        @JsonProperty("articulo_69b_bis") ArticleStats article69bBis
) {
}
