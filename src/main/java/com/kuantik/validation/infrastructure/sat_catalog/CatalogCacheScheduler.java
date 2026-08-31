package com.kuantik.validation.infrastructure.sat_catalog;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Limpia el caché de archivos JSON de catálogos SAT cada noche a las 23:00 hora México.
 * También evicta el caché L1 (Caffeine) para forzar re-fetch fresco al día siguiente.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class CatalogCacheScheduler {

    private final CatalogFileCache catalogFileCache;
    private final CacheManager cacheManager;

    @Scheduled(cron = "0 0 23 * * *", zone = "America/Mexico_City")
    public void evictCatalogCache() {
        log.info("CatalogCacheScheduler — iniciando limpieza nocturna de catálogos SAT");
        this.catalogFileCache.evictAll();
        evictL1Cache("sat-catalogs");
        evictL1Cache("sat-catalogs-pages");
        log.info("CatalogCacheScheduler — limpieza completada");
    }

    private void evictL1Cache(String cacheName) {
        var l1Cache = this.cacheManager.getCache(cacheName);
        if (l1Cache != null) {
            l1Cache.clear();
            log.info("CatalogCacheScheduler — caché L1 '{}' limpiado", cacheName);
        }
    }
}
