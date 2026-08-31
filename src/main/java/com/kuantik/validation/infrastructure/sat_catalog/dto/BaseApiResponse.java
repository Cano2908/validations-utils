package com.kuantik.validation.infrastructure.sat_catalog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Envoltorio genérico de respuesta para todos los endpoints del ecosistema Kuantik.
 *
 * @param <T> tipo del payload en {@code data}
 */
public record BaseApiResponse<T>(
        boolean success,
        String message,
        T data,
        @JsonProperty("status_code") int statusCode
) {
}
