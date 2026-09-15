package com.kuantik.validation.infrastructure.blacklist.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class CollectionMatch {

    @JsonProperty("coleccion")
    private String collection;

    @JsonProperty("etiqueta")
    private String label;

    @JsonProperty("registros")
    private List<Map<String, Object>> records;
}
