package com.kuantik.validation.infrastructure.sat_catalog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Cuerpo de la petición POST {@code /catalogos/uso-cfdi/buscar}.
 */
public record BuscarUsoCFDIRequest(
        @JsonProperty("claves") List<String> claves,
        @JsonProperty("regimen_fiscal_receptor") String regimenFiscalReceptor,
        @JsonProperty("tipo_persona") String tipoPersona
) {
}
