package com.kuantik.validation.infrastructure.config;

public interface RestServiceProperties {
    String baseUrl();

    int connectTimeoutSeconds();

    int readTimeoutSeconds();
}
