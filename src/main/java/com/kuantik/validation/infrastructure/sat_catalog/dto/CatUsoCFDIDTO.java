package com.kuantik.validation.infrastructure.sat_catalog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Entrada del catálogo {@code cfdi_usocfdi}.
 */
public record CatUsoCFDIDTO(
        @JsonProperty("c_UsoCFDI") String clave,
        @JsonProperty("Descripción") String descripcion,
        @JsonProperty("Aplica para tipo persona") String aplicaParaTipoPersona,
        @JsonProperty("Fecha inicio de vigencia") String fechaInicioVigencia,
        @JsonProperty("Fecha fin de vigencia") String fechaFinVigencia,
        @JsonProperty("Régimen Fiscal Receptor") String regimenFiscalReceptor
) implements CatalogoVigente {
}
