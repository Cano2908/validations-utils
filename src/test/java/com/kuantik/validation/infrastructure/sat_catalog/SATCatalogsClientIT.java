package com.kuantik.validation.infrastructure.sat_catalog;

import com.kuantik.validation.infrastructure.sat_catalog.dto.CatExportacionDTO;
import com.kuantik.validation.infrastructure.sat_catalog.dto.CatObjetoImpDTO;
import com.kuantik.validation.infrastructure.sat_catalog.dto.CatRegimenFiscalDTO;
import com.kuantik.validation.infrastructure.sat_catalog.dto.CatUsoCFDIDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests de integración contra el endpoint real de preprod.
 * Requieren conexión a internet. Para ejecutar:
 * <pre>./mvnw test -Dtest=SATCatalogsClientIT</pre>
 */
@Tag("integration")
class SATCatalogsClientIT {

    private static SATCatalogsRestClient client;

    private static String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }

    @BeforeAll
    static void buildClient() {
        var httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        var requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofSeconds(15));

        var restClient = RestClient.builder()
                .baseUrl("https://preprod.kuantik.mx/api/v1/catalogos_sat")
                .requestFactory(requestFactory)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();

        client = new SATCatalogsRestClient(restClient, mock(CatalogFileCache.class));
    }

    @Test
    void shouldReturnAllCatalogNames() {
        List<String> catalogs = client.getAllCatalogs();

        assertThat(catalogs)
                .isNotEmpty()
                .hasSizeGreaterThan(10)
                .contains(
                        "cfdi_regimenfiscal",
                        "cfdi_usocfdi",
                        "cfdi_moneda",
                        "cfdi_meses",
                        "cfdi_formapago",
                        "cfdi_tipodecomprobante"
                );

        System.out.println("\n══════ Catálogos disponibles (" + catalogs.size() + ") ══════");
        catalogs.forEach(name -> System.out.println("  • " + name));
    }

    @Test
    void shouldReturnCfdiRegimenfiscalWithAllFields() {
        List<CatRegimenFiscalDTO> items = client.getCatalogByName(
                "cfdi_regimenfiscal", 1, 50, null, CatRegimenFiscalDTO.class);

        assertThat(items)
                .isNotEmpty()
                .allSatisfy(item -> assertThat(item.clave()).isNotBlank());

        System.out.println("\n══════ cfdi_regimenfiscal (" + items.size() + " registros) ══════");
        System.out.printf("%-6s | %-55s | %-6s | %-5s%n", "Clave", "Descripción", "Física", "Moral");
        System.out.println("-".repeat(80));
        items.forEach(item -> System.out.printf("%-6s | %-55s | %-6s | %-5s%n",
                item.clave(), item.descripcion(), item.fisica(), item.moral()));
    }

    @Test
    void shouldReturnCfdiUsoCFDIWithRegimenFiscalReceptor() {
        List<CatUsoCFDIDTO> items = client.getCatalogByName(
                "cfdi_usocfdi", 1, 50, null, CatUsoCFDIDTO.class);

        assertThat(items)
                .isNotEmpty()
                .filteredOn(item -> item.clave() != null && !item.clave().isBlank())
                .allSatisfy(item -> assertThat(item.descripcion()).isNotBlank());

        System.out.println("\n══════ cfdi_usocfdi (" + items.size() + " registros) ══════");
        items.stream()
                .filter(item -> item.clave() != null && !item.clave().isBlank())
                .forEach(item -> System.out.printf("%-8s | %-45s | %-10s | %s%n",
                        item.clave(), truncate(item.descripcion(), 45),
                        item.aplicaParaTipoPersona(), item.regimenFiscalReceptor()));
    }

    @Test
    void shouldReturnCfdiObjetoImpWithAllClaves() {
        List<CatObjetoImpDTO> items = client.getCatalogByName(
                "cfdi_objetoimp", 1, 20, null, CatObjetoImpDTO.class);

        assertThat(items)
                .isNotEmpty()
                .allSatisfy(item -> assertThat(item.clave()).isNotBlank());

        System.out.println("\n══════ cfdi_objetoimp (" + items.size() + " registros) ══════");
        items.forEach(item -> System.out.printf("%-4s | %s%n", item.clave(), item.descripcion()));
    }

    @Test
    void shouldReturnCfdiExportacionWithAllClaves() {
        List<CatExportacionDTO> items = client.getCatalogByName(
                "cfdi_exportacion", 1, 10, null, CatExportacionDTO.class);

        assertThat(items)
                .isNotEmpty()
                .allSatisfy(item -> assertThat(item.clave()).isNotBlank());

        System.out.println("\n══════ cfdi_exportacion (" + items.size() + " registros) ══════");
        items.forEach(item -> System.out.printf("%-4s | %s%n", item.clave(), item.descripcion()));
    }
}
