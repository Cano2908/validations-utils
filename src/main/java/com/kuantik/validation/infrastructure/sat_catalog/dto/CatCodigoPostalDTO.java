package com.kuantik.validation.infrastructure.sat_catalog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Entrada del catálogo {@code cfdi_codigopostal} (c_CodigoPostal).
 */
public record CatCodigoPostalDTO(
        @JsonProperty("c_CodigoPostal") String clave
) {
}
