package com.kuantik.validation.infrastructure.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Slf4j
@Component
public class RestClientFactory {

    public RestClient build(RestServiceProperties properties, String clientName) {
        return build(properties, clientName, null);
    }

    public RestClient build(RestServiceProperties properties, String clientName, String bearerToken) {
        HttpClient jdkHttpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(properties.connectTimeoutSeconds()))
                .build();

        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(jdkHttpClient);
        factory.setReadTimeout(Duration.ofSeconds(properties.readTimeoutSeconds()));

        log.info("{} inicializado — baseUrl={} connectTimeout={}s readTimeout={}s auth={}",
                clientName,
                properties.baseUrl(),
                properties.connectTimeoutSeconds(),
                properties.readTimeoutSeconds(),
                bearerToken != null && !bearerToken.isBlank() ? "Bearer ***" : "none");

        RestClient.Builder builder = RestClient.builder()
                .baseUrl(properties.baseUrl())
                .requestFactory(factory)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        if (bearerToken != null && !bearerToken.isBlank()) {
            builder.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken);
        }

        return builder.build();
    }

    public ObjectMapper buildObjectMapper() {
        return new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}
