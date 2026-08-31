package com.kuantik.validation.infrastructure.sat_catalog;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuantik.validation.infrastructure.sat_catalog.config.SATCatalogsProperties;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * Caché de segundo nivel para catálogos SAT basado en archivos JSON.
 *
 * <p>Flujo de lectura en {@link SATCatalogsRestClient#getAllCatalogItems}:</p>
 * <ol>
 *   <li>L1 — Caffeine (in-memory, TTL 4h)</li>
 *   <li>L2 — Archivo JSON en {@code cache/sat-catalogs/{catalogo}.json}</li>
 *   <li>L3 — HTTP al API de catálogos SAT</li>
 * </ol>
 */
@Slf4j
@Component
public class CatalogFileCache {

    private final Path cacheDir;
    private final ObjectMapper objectMapper;

    public CatalogFileCache(SATCatalogsProperties properties,
                            @Qualifier("satCatalogsObjectMapper") ObjectMapper objectMapper) {
        this.cacheDir = Path.of(properties.cachePath());
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    void init() {
        try {
            Files.createDirectories(this.cacheDir);
            log.info("CatalogFileCache inicializado — directorio: {}", this.cacheDir.toAbsolutePath());
        } catch (IOException e) {
            log.warn("CatalogFileCache — no se pudo crear el directorio {}: {}", this.cacheDir, e.getMessage());
        }
    }

    public <T> Optional<List<T>> get(String catalogo, Class<T> itemType) {
        Path file = this.cacheDir.resolve(catalogo + ".json");
        if (!Files.exists(file)) {
            return Optional.empty();
        }
        try {
            JavaType type = this.objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, itemType);
            List<T> items = this.objectMapper.readValue(file.toFile(), type);
            log.info("CatalogFileCache HIT — '{}' {} ítems desde {}", catalogo, items.size(), file.getFileName());
            return Optional.of(items);
        } catch (IOException e) {
            log.warn("CatalogFileCache — error al leer '{}', se re-fetcha desde API: {}", catalogo, e.getMessage());
            return Optional.empty();
        }
    }

    public <T> void put(String catalogo, List<T> items) {
        Path file = this.cacheDir.resolve(catalogo + ".json");
        try {
            this.objectMapper.writeValue(file.toFile(), items);
            log.info("CatalogFileCache WRITE — '{}' {} ítems guardados en {}", catalogo, items.size(), file.getFileName());
        } catch (IOException e) {
            log.warn("CatalogFileCache — error al escribir '{}': {}", catalogo, e.getMessage());
        }
    }

    public void evictAll() {
        try (var directoryStream = Files.list(this.cacheDir)) {
            List<Path> jsonFiles = directoryStream
                    .filter(filePath -> filePath.toString().endsWith(".json"))
                    .toList();
            int deleted = 0;
            for (Path jsonFile : jsonFiles) {
                try {
                    Files.delete(jsonFile);
                    log.info("CatalogFileCache EVICT — eliminado: {}", jsonFile.getFileName());
                    deleted++;
                } catch (IOException ioException) {
                    log.warn("CatalogFileCache — no se pudo eliminar {}: {}", jsonFile.getFileName(), ioException.getMessage());
                }
            }
            log.info("CatalogFileCache EVICT — {} archivos eliminados", deleted);
        } catch (IOException e) {
            log.warn("CatalogFileCache — error al listar directorio para evict: {}", e.getMessage());
        }
    }
}
