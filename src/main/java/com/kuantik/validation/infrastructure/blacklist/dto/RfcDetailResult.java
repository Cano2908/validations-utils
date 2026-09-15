package com.kuantik.validation.infrastructure.blacklist.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RfcDetailResult {

    private String rfc;

    @JsonProperty("encontrado")
    private boolean found;

    @JsonProperty("coincidencias")
    private List<CollectionMatch> matches;
}
