package com.kuantik.validation.infrastructure.sat_catalog.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Propiedades de conexión hacia el servicio de catálogos SAT.
 * Se leen del bloque {@code sat-catalogs:} en {@code application.yaml}
 * o de variables de entorno con el prefijo {@code SAT_CATALOGS_}.
 */
@ConfigurationProperties(prefix = "sat-catalogs")
public record SATCatalogsProperties(
        @DefaultValue("http://sat-catalogs:8080") String baseUrl,
        @DefaultValue("5") int connectTimeoutSeconds,
        @DefaultValue("10") int readTimeoutSeconds,
        @DefaultValue("") String apiKey,
        @DefaultValue("cache/sat-catalogs") String cachePath
) {
}
