package com.kuantik.validation.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ValidationJob {
    private final String jobId;
    private final String companyId;
    private final String xmlContent;
    private final String jsonContent;
    private final String traceId;
    private final List<String> ruleCodes;
}
