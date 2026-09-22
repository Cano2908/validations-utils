package com.kuantik.validation.infrastructure.sat_catalog.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuantik.validation.infrastructure.config.RestClientFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@ConditionalOnProperty(name = "sat-catalogs.base-url")
@EnableConfigurationProperties(SATCatalogsProperties.class)
class SATCatalogsConfig {

    @Bean("satCatalogsObjectMapper")
    ObjectMapper satCatalogsObjectMapper(RestClientFactory factory) {
        return factory.buildObjectMapper();
    }

    @Bean("satCatalogsRestClient")
    RestClient satCatalogsRestClient(SATCatalogsProperties properties, RestClientFactory factory) {
        return factory.build(properties, "SATCatalogsRestClient");
    }
}
