package com.kuantik.validation.infrastructure.sat_catalog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuantik.validation.infrastructure.sat_catalog.dto.CatalogPageResponse;
import com.kuantik.validation.infrastructure.sat_catalog.exception.SATCatalogsClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementación HTTP de {@link SATCatalogsClient} basada en {@link RestClient}.
 *
 * <p>Deserializa cada página como {@code List<Map<String, Object>>} y luego convierte
 * cada mapa al tipo {@code T} via {@code ObjectMapper.convertValue()}.</p>
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "sat-catalogs.base-url")
public class SATCatalogsRestClient implements SATCatalogsClient {

    private static final ParameterizedTypeReference<CatalogPageResponse<Map<String, Object>>> PAGE_TYPE =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final CatalogFileCache catalogFileCache;

    public SATCatalogsRestClient(@Qualifier("satCatalogsRestClient") RestClient restClient,
                                 @Qualifier("satCatalogsObjectMapper") ObjectMapper objectMapper,
                                 CatalogFileCache catalogFileCache) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
        this.catalogFileCache = catalogFileCache;
    }

    @Override
    public List<String> getAllCatalogs() {
        try {
            List<String> catalogNames = this.restClient.get()
                    .uri("")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<String>>() {
                    });

            if (catalogNames == null || catalogNames.isEmpty()) {
                log.warn("getAllCatalogs — respuesta vacía");
                return List.of();
            }

            return catalogNames;

        } catch (RestClientException exception) {
            throw new SATCatalogsClientException(
                    "Error al consultar lista de catálogos: %s".formatted(exception.getMessage()),
                    exception);
        }
    }

    @Override
    @Cacheable(value = "sat-catalogs-pages", key = "#catalogo + '-' + #page + '-' + #pageSize + '-' + (#itemFilter ?: '')")
    public <T> List<T> getCatalogByName(String catalogo, int page, int pageSize, String itemFilter, Class<T> itemType) {
        try {
            CatalogPageResponse<Map<String, Object>> catalogPageResponse = this.restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/{catalogo}")
                            .queryParam("page", page)
                            .queryParam("pageSize", pageSize)
                            .queryParamIfPresent("search", Optional.ofNullable(itemFilter))
                            .build(catalogo))
                    .retrieve()
                    .body(PAGE_TYPE);

            if (catalogPageResponse == null || catalogPageResponse.data() == null || catalogPageResponse.data().isEmpty()) {
                log.warn("getCatalogByName catalogo={} — respuesta vacía", catalogo);
                return List.of();
            }

            return catalogPageResponse.data().stream()
                    .map(item -> this.objectMapper.convertValue(item, itemType))
                    .toList();

        } catch (RestClientException exception) {
            throw new SATCatalogsClientException(
                    "Error al consultar catálogo %s (page=%d): %s".formatted(catalogo, page, exception.getMessage()),
                    exception);
        }
    }

    @Override
    @Cacheable(value = "sat-catalogs", key = "#catalogo")
    public <T> List<T> getAllCatalogItems(String catalogo, int pageSize, Class<T> itemType) {
        Optional<List<T>> cachedItems = this.catalogFileCache.get(catalogo, itemType);
        if (cachedItems.isPresent()) {
            return cachedItems.get();
        }
        List<T> all = new ArrayList<>();
        int page = 1;
        List<T> batch;
        do {
            batch = this.getCatalogByName(catalogo, page++, pageSize, null, itemType);
            all.addAll(batch);
        } while (batch.size() == pageSize);
        this.catalogFileCache.put(catalogo, all);
        log.info("sat-catalogs HTTP — '{}' {} ítems cargados y guardados en caché", catalogo, all.size());
        return all;
    }
}
