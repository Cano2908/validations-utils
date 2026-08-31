package com.kuantik.validation.domain.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Rule {
    private final String ruleId;
    private final String name;
    private final String code;
    private final String severity;
}
