package com.kuantik.validation.infrastructure.sat_catalog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Entrada del catálogo {@code cfdi_moneda} (ISO 4217 adoptado por el SAT).
 */
public record CatMonedaDTO(
        @JsonProperty("c_Moneda") String clave,
        @JsonProperty("Descripción") String descripcion
) {
}
