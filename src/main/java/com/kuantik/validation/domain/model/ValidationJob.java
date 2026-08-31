package com.kuantik.validation.domain.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ValidationJob {
    private final String jobId;
    private final String companyId;
    private final String xmlContent;
    private final String traceId;
}
