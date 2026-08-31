package com.kuantik.validation.infrastructure.sat_catalog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Entrada del catálogo {@code cfdi_claveunidad} (c_ClaveUnidad).
 */
public record CatClaveUnidadDTO(
        @JsonProperty("c_ClaveUnidad") String clave,
        @JsonProperty("Nombre") String nombre,
        @JsonProperty("Descripción") String descripcion,
        @JsonProperty("Símbolo") String simbolo,
        @JsonProperty("Fecha de inicio de vigencia") String fechaInicioVigencia,
        @JsonProperty("Fecha de fin de vigencia") String fechaFinVigencia
) implements CatalogoVigente {
}
