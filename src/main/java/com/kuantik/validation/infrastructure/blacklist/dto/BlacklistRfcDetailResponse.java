package com.kuantik.validation.infrastructure.blacklist.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record BlacklistRfcDetailResponse(
        boolean success,
        @JsonProperty("resultados") List<RfcDetailResult> results
) {
}
