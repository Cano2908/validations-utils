package com.kuantik.validation.infrastructure.blacklist.dto;

import java.util.List;

public record BlacklistRfcDetailRequest(
        String rfcUsuario,
        List<String> rfcs
) {
}
