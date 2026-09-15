package com.kuantik.validation.infrastructure.blacklist.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record BlacklistRfcExistsResponse(
        boolean success,
        @JsonProperty("resultados") List<RfcResult> results
) {
}
