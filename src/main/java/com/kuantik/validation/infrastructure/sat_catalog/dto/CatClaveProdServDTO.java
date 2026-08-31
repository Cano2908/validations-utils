package com.kuantik.validation.infrastructure.sat_catalog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO del catálogo {@code cfdi_claveprodserv} (c_ClaveProdServ).
 */
public record CatClaveProdServDTO(
        @JsonProperty("c_ClaveProdServ") String clave,
        @JsonProperty("Descripción") String descripcion,
        @JsonProperty("FechaInicioVigencia") String fechaInicioVigencia,
        @JsonProperty("FechaFinVigencia") String fechaFinVigencia
) implements CatalogoVigente {
}
