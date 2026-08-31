package com.kuantik.validation.infrastructure.sat_catalog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Entrada del catálogo {@code cfdi_formapago} (c_FormaPago).
 */
public record CatFormaPagoDTO(
        @JsonProperty("c_FormaPago") String clave,
        @JsonProperty("Descripción") String descripcion,
        @JsonProperty("Bancarizado") String bancarizado,
        @JsonProperty("Fecha de inicio de vigencia") String fechaInicioVigencia,
        @JsonProperty("Fecha de fin de vigencia") String fechaFinVigencia
) implements CatalogoVigente {
}
