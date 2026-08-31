package com.kuantik.validation.infrastructure.sat_catalog;

import com.kuantik.validation.infrastructure.sat_catalog.exception.SATCatalogsClientException;

import java.util.List;

/**
 * Puerto de salida hacia el servicio de catálogos SAT.
 * Las reglas solo dependen de esta interfaz; la implementación es la única
 * que conoce el protocolo HTTP.
 *
 * <p>Todos los métodos lanzan {@link SATCatalogsClientException} si el servicio
 * no está disponible o responde con error — las reglas deben capturarla y
 * retornar un {@code RuleResult} descriptivo en lugar de propagar la excepción.</p>
 */
public interface SATCatalogsClient {
    /**
     * GET /api/v1/catalogos
     *
     * @return lista de nombres de catálogos disponibles
     * @throws SATCatalogsClientException si el servicio no responde o responde con error HTTP
     */
    List<String> getAllCatalogs();

    /**
     * GET {@code /api/v1/catalogos/{catalogo}?page={page}&pageSize={pageSize}}
     *
     * @param <T>        tipo de los ítems de respuesta
     * @param catalogo   nombre del catálogo SAT (ej. {@code "cfdi_regimenfiscal"})
     * @param page       página solicitada (1-indexed — page=0 retorna HTTP 500 del servidor)
     * @param pageSize   elementos por página
     * @param itemFilter si no es {@code null}, se envía como {@code ?search=itemFilter}
     * @param itemType   clase concreta a la que se deserializa cada ítem
     * @return lista de ítems deserializados; vacía si el servicio no devuelve datos
     * @throws SATCatalogsClientException si el servicio no responde o responde con error HTTP
     */
    <T> List<T> getCatalogByName(String catalogo, int page, int pageSize, String itemFilter, Class<T> itemType);

    /**
     * Obtiene todos los ítems de un catálogo iterando las páginas hasta agotar los datos.
     * Incluye integración con caché L2 (archivo JSON) y L1 (Caffeine).
     */
    <T> List<T> getAllCatalogItems(String catalogo, int pageSize, Class<T> itemType);
}
