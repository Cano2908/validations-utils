package com.kuantik.validation.infrastructure.blacklist.config;

import com.kuantik.validation.infrastructure.config.RestServiceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "blacklist")
public record BlacklistProperties(
        String baseUrl,
        int connectTimeoutSeconds,
        int readTimeoutSeconds
) implements RestServiceProperties {
}
