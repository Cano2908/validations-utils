package com.kuantik.validation.infrastructure.sat_catalog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Catálogo {@code cfdi_objetoimp} del SAT.
 */
public record CatObjetoImpDTO(
        @JsonProperty("c_ObjetoImp") String clave,
        @JsonProperty("Descripción") String descripcion,
        @JsonProperty("Fecha inicio de vigencia") String fechaInicioVigencia,
        @JsonProperty("Fecha fin de vigencia") String fechaFinVigencia
) {
}
