package com.kuantik.validation.infrastructure.blacklist.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuantik.validation.infrastructure.config.RestClientFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@ConditionalOnProperty(name = "blacklist.base-url")
@EnableConfigurationProperties(BlacklistProperties.class)
public class BlacklistConfig {

    @Bean("blacklistObjectMapper")
    ObjectMapper blacklistObjectMapper(RestClientFactory factory) {
        return factory.buildObjectMapper();
    }

    @Bean("blacklistRestClient")
    RestClient blacklistRestClient(BlacklistProperties properties, RestClientFactory factory) {
        return factory.build(properties, "BlacklistRestClient");
    }
}
