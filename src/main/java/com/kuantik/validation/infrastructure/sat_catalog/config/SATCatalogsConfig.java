package com.kuantik.validation.infrastructure.sat_catalog.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Slf4j
@Configuration
class SATCatalogsConfig {

    @Bean("satCatalogsRestClient")
    RestClient satCatalogsRestClient(SATCatalogsProperties properties) {
        var httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(properties.connectTimeoutSeconds()))
                .build();

        var requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofSeconds(properties.readTimeoutSeconds()));

        log.info("SATCatalogsRestClient inicializado — baseUrl={} connectTimeout={}s readTimeout={}s",
                properties.baseUrl(),
                properties.connectTimeoutSeconds(),
                properties.readTimeoutSeconds());

        return RestClient.builder()
                .baseUrl(properties.baseUrl())
                .requestFactory(requestFactory)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
