package com.kuantik.validation.infrastructure.sat_catalog.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Activa Spring Cache con Caffeine como backend.
 *
 * <p>Dos regiones de caché con capacidades independientes:</p>
 * <ul>
 *   <li>{@code sat-catalogs} — lista completa por catálogo. TTL 4h, máx 30.</li>
 *   <li>{@code sat-catalogs-pages} — páginas individuales. TTL 4h, máx 500.</li>
 * </ul>
 */
@EnableCaching
@Configuration
public class CatalogCacheConfig {

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager simpleCacheManager = new SimpleCacheManager();
        simpleCacheManager.setCaches(List.of(
                caffeineCache("sat-catalogs", 30),
                caffeineCache("sat-catalogs-pages", 500)
        ));
        return simpleCacheManager;
    }

    private CaffeineCache caffeineCache(String name, int maximumSize) {
        return new CaffeineCache(name, Caffeine.newBuilder()
                .expireAfterWrite(4, TimeUnit.HOURS)
                .maximumSize(maximumSize)
                .build());
    }
}
