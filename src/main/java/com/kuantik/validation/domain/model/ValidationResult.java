package com.kuantik.validation.domain.model;

import lombok.Getter;

import java.util.List;

@Getter
public class ValidationResult {
    private final String overallStatus;
    private final List<RuleResult> results;
    private final String errorMessage;

    private ValidationResult(String overallStatus, List<RuleResult> results, String errorMessage) {
        this.overallStatus = overallStatus;
        this.results = results;
        this.errorMessage = errorMessage;
    }

    public static ValidationResult failed(String errorMessage) {
        return new ValidationResult("FAILED", List.of(), errorMessage);
    }

    public static ValidationResult completed(List<RuleResult> ruleResults) {
        boolean hasErrorFailure = ruleResults.stream()
                .anyMatch(r -> !r.isPassed() && "ERROR".equals(r.getSeverity()));
        String status = hasErrorFailure ? "PARTIAL" : "COMPLETED";
        return new ValidationResult(status, ruleResults, "");
    }
}
