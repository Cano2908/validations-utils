package com.kuantik.validation.infrastructure.sat_catalog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Entrada del catálogo {@code cfdi_regimenfiscal}.
 */
public record CatRegimenFiscalDTO(
        @JsonProperty("c_RegimenFiscal") String clave,
        @JsonProperty("Descripción") String descripcion,
        @JsonProperty("Física") String fisica,
        @JsonProperty("Moral") String moral,
        @JsonProperty("Fecha de inicio de vigencia") String fechaInicioVigencia,
        @JsonProperty("Fecha de fin de vigencia") String fechaFinVigencia
) implements CatalogoVigente {
}
