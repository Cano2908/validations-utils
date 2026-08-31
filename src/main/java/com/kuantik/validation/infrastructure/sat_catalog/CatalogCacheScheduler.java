package com.kuantik.validation.infrastructure.sat_catalog;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Limpia el caché de archivos JSON de catálogos SAT cada noche a las 23:00 hora México.
 */
@Slf4j
@Component
public class CatalogCacheScheduler {

    private final CatalogFileCache catalogFileCache;

    public CatalogCacheScheduler(CatalogFileCache catalogFileCache) {
        this.catalogFileCache = catalogFileCache;
    }

    @Scheduled(cron = "0 0 23 * * *", zone = "America/Mexico_City")
    public void evictCatalogCache() {
        log.info("CatalogCacheScheduler — iniciando limpieza nocturna de catálogos SAT");
        this.catalogFileCache.evictAll();
        log.info("CatalogCacheScheduler — limpieza completada");
    }
}
