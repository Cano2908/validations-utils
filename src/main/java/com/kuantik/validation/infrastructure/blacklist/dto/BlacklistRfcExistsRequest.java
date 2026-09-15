package com.kuantik.validation.infrastructure.blacklist.dto;

public record BlacklistRfcExistsRequest(
        String rfc,
        boolean saveHistory
) {
}
