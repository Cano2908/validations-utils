package com.kuantik.validation.infrastructure.blacklist.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ArticleStats(
        @JsonProperty("encontradas") int found,
        @JsonProperty("noEncontradas") int notFound
) {
}
