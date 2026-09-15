package com.kuantik.validation.infrastructure.blacklist.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Per-RFC result from {@code POST /api/v2/listas_negras/rfc/existe}.
 *
 * Dynamic {@code sat_*} boolean fields are captured in {@link #collections} via {@code @JsonAnySetter}.
 * {@link #articleByCollection} maps each collection name to its CFF article ("69", "69-B", etc.)
 * or {@code null} for administrative collections with no article.
 */
@Getter
@Setter
public class RfcResult {

    private String rfc;

    @JsonProperty("nombre")
    private String name;

    @JsonProperty("encontrado")
    private boolean found;

    @JsonProperty("_meta")
    private RfcMeta meta;

    private DataCreation dataCreation;

    @JsonProperty("articuloPorColeccion")
    private Map<String, String> articleByCollection;

    private final Map<String, Boolean> collections = new LinkedHashMap<>();

    @JsonAnySetter
    public void addCollection(String key, Object value) {
        if (value instanceof Boolean b) {
            collections.put(key, b);
        }
    }
}
