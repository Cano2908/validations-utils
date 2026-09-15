package com.kuantik.validation.infrastructure.blacklist;

import com.kuantik.validation.infrastructure.blacklist.config.BlacklistProperties;
import com.kuantik.validation.infrastructure.blacklist.dto.BlacklistRfcDetailRequest;
import com.kuantik.validation.infrastructure.blacklist.dto.BlacklistRfcDetailResponse;
import com.kuantik.validation.infrastructure.blacklist.dto.BlacklistRfcExistsRequest;
import com.kuantik.validation.infrastructure.blacklist.dto.BlacklistRfcExistsResponse;
import com.kuantik.validation.infrastructure.blacklist.exception.BlacklistClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Slf4j
@Component
public class BlacklistClientRest implements BlacklistClient {

    private final RestClient restClient;

    public BlacklistClientRest(
            @Qualifier("blacklistRestClient") RestClient restClient,
            BlacklistProperties properties) {
        this.restClient = restClient;
    }

    @Override
    public BlacklistRfcExistsResponse verifyRfcExistence(String rfc) {
        BlacklistRfcExistsRequest request = new BlacklistRfcExistsRequest(rfc, false);
        try {
            BlacklistRfcExistsResponse response = this.restClient.post()
                    .uri("/rfc/existe")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(BlacklistRfcExistsResponse.class);

            if (response == null || response.results() == null) {
                log.warn("verifyRfcExistence — empty response for rfc={}", rfc);
                return new BlacklistRfcExistsResponse(false, List.of());
            }

            return response;

        } catch (RestClientException e) {
            throw new BlacklistClientException(
                    "Error querying blacklists for rfc=%s: %s".formatted(rfc, e.getMessage()), e);
        }
    }

    @Override
    public BlacklistRfcDetailResponse verifyRfcDetail(String rfc) {
        BlacklistRfcDetailRequest request = new BlacklistRfcDetailRequest("ABC010101ABC", List.of(rfc));
        try {
            BlacklistRfcDetailResponse response = this.restClient.post()
                    .uri("/rfc/detalle")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(BlacklistRfcDetailResponse.class);

            if (response == null || response.results() == null) {
                log.warn("verifyRfcDetail — empty response for rfc={}", rfc);
                return new BlacklistRfcDetailResponse(false, List.of());
            }

            return response;

        } catch (RestClientException e) {
            throw new BlacklistClientException(
                    "Error querying blacklist detail for rfc=%s: %s".formatted(rfc, e.getMessage()), e);
        }
    }
}
