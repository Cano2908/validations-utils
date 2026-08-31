package com.kuantik.validation.infrastructure.sat_catalog.dto;

import java.util.List;

/**
 * Respuesta paginada del endpoint {@code GET /api/v1/catalogos/{catalogo}}.
 *
 * @param <T> tipo de los ítems en {@code data}
 */
public record CatalogPageResponse<T>(
        List<T> data,
        int total
) {
}
