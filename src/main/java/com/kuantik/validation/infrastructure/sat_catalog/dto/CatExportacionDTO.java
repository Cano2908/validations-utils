package com.kuantik.validation.infrastructure.sat_catalog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Catálogo {@code cfdi_exportacion} del SAT.
 */
public record CatExportacionDTO(
        @JsonProperty("c_Exportacion") String clave,
        @JsonProperty("Descripción") String descripcion,
        @JsonProperty("Fecha inicio de vigencia") String fechaInicioVigencia,
        @JsonProperty("Fecha fin de vigencia") String fechaFinVigencia
) {
}
