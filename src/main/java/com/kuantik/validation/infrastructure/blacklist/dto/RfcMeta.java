package com.kuantik.validation.infrastructure.blacklist.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RfcMeta(
        @JsonProperty("listasEncontradas") int listsFound,
        @JsonProperty("listasNoEncontradas") int listsNotFound,
        @JsonProperty("articulos") ArticlesStats articles
) {
}
