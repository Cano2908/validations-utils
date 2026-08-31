package com.kuantik.validation.infrastructure.sat_catalog;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuantik.validation.infrastructure.sat_catalog.dto.CatRegimenFiscalDTO;
import com.kuantik.validation.infrastructure.sat_catalog.exception.SATCatalogsClientException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Tests unitarios de {@link SATCatalogsRestClient#getCatalogByName}.
 * No necesita red — {@link MockRestServiceServer} intercepta las llamadas HTTP.
 */
class SATCatalogsRestClientTest {

    private MockRestServiceServer server;
    private SATCatalogsRestClient client;

    private static String cfdiRegimenfiscalJson() {
        return """
                {
                  "data": [
                    {
                      "c_RegimenFiscal": "601",
                      "Descripción": "General de Ley Personas Morales",
                      "Física": "No",
                      "Moral": "Sí",
                      "Fecha de inicio de vigencia": "2022-01-01 00:00:00",
                      "Fecha de fin de vigencia": ""
                    },
                    {
                      "c_RegimenFiscal": "605",
                      "Descripción": "Sueldos y Salarios e Ingresos Asimilados a Salarios",
                      "Física": "Sí",
                      "Moral": "No",
                      "Fecha de inicio de vigencia": "2022-01-01 00:00:00",
                      "Fecha de fin de vigencia": ""
                    }
                  ],
                  "total": 19
                }
                """;
    }

    @BeforeEach
    void setUp() {
        RestClient.Builder restClientBuilder = RestClient.builder().baseUrl("http://sat-test");
        this.server = MockRestServiceServer.bindTo(restClientBuilder).build();
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.client = new SATCatalogsRestClient(restClientBuilder.build(), objectMapper, mock(CatalogFileCache.class));
    }

    @Test
    void shouldDeserializeCfdiRegimenfiscalCorrectly() {
        server.expect(requestTo("http://sat-test/cfdi_regimenfiscal?page=1&pageSize=10"))
                .andRespond(withSuccess(cfdiRegimenfiscalJson(), MediaType.APPLICATION_JSON));

        List<CatRegimenFiscalDTO> result = client.getCatalogByName(
                "cfdi_regimenfiscal", 1, 10, null, CatRegimenFiscalDTO.class);

        assertThat(result).hasSize(2);

        CatRegimenFiscalDTO regimen601 = result.get(0);
        assertThat(regimen601.clave()).isEqualTo("601");
        assertThat(regimen601.descripcion()).isEqualTo("General de Ley Personas Morales");
        assertThat(regimen601.fisica()).isEqualTo("No");
        assertThat(regimen601.moral()).isEqualTo("Sí");
        assertThat(regimen601.fechaInicioVigencia()).isEqualTo("2022-01-01 00:00:00");
        assertThat(regimen601.fechaFinVigencia()).isEmpty();

        CatRegimenFiscalDTO regimen605 = result.get(1);
        assertThat(regimen605.clave()).isEqualTo("605");
        assertThat(regimen605.descripcion()).isEqualTo("Sueldos y Salarios e Ingresos Asimilados a Salarios");
        assertThat(regimen605.fisica()).isEqualTo("Sí");
        assertThat(regimen605.moral()).isEqualTo("No");

        server.verify();
    }

    @Test
    void shouldReturnEmptyListWhenDataIsEmpty() {
        server.expect(requestTo("http://sat-test/cfdi_regimenfiscal?page=1&pageSize=10"))
                .andRespond(withSuccess("""
                        {"data": [], "total": 0}
                        """, MediaType.APPLICATION_JSON));

        List<CatRegimenFiscalDTO> result = client.getCatalogByName(
                "cfdi_regimenfiscal", 1, 10, null, CatRegimenFiscalDTO.class);

        assertThat(result).isEmpty();
        server.verify();
    }

    @Test
    void shouldThrowSATCatalogsClientExceptionOnServerError() {
        server.expect(requestTo("http://sat-test/cfdi_regimenfiscal?page=1&pageSize=10"))
                .andRespond(withServerError());

        assertThatThrownBy(() -> client.getCatalogByName(
                "cfdi_regimenfiscal", 1, 10, null, CatRegimenFiscalDTO.class))
                .isInstanceOf(SATCatalogsClientException.class)
                .hasMessageContaining("cfdi_regimenfiscal");

        server.verify();
    }
}
