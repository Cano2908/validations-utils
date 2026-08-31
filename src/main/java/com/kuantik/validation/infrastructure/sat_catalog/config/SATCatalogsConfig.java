package com.kuantik.validation.infrastructure.sat_catalog.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
@EnableConfigurationProperties(SATCatalogsProperties.class)
class SATCatalogsConfig {

    @Bean("satCatalogsObjectMapper")
    ObjectMapper satCatalogsObjectMapper() {
        return new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Bean("satCatalogsRestClient")
    RestClient satCatalogsRestClient(SATCatalogsProperties properties) {
        HttpClient jdkHttpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(properties.connectTimeoutSeconds()))
                .build();

        JdkClientHttpRequestFactory jdkRequestFactory = new JdkClientHttpRequestFactory(jdkHttpClient);
        jdkRequestFactory.setReadTimeout(Duration.ofSeconds(properties.readTimeoutSeconds()));

        log.info("SATCatalogsRestClient inicializado — baseUrl={} connectTimeout={}s readTimeout={}s",
                properties.baseUrl(),
                properties.connectTimeoutSeconds(),
                properties.readTimeoutSeconds());

        return RestClient.builder()
                .baseUrl(properties.baseUrl())
                .requestFactory(jdkRequestFactory)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
