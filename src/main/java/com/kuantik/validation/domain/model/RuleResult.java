package com.kuantik.validation.domain.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RuleResult {
    private final String ruleId;
    private final String code;
    private final boolean passed;
    private final String message;
    private final String label;
    private final String severity;
    private final Object metadata;

    public static RuleResult success(Rule rule, String message) {
        return RuleResult.builder()
                .ruleId(rule.getRuleId())
                .code(rule.getCode())
                .passed(true)
                .message(message)
                .severity(rule.getSeverity())
                .build();
    }

    public static RuleResult failure(Rule rule, String message) {
        return RuleResult.builder()
                .ruleId(rule.getRuleId())
                .code(rule.getCode())
                .passed(false)
                .message(message)
                .severity(rule.getSeverity())
                .build();
    }

    public static RuleResult notImplemented(Rule rule) {
        return RuleResult.builder()
                .ruleId(rule.getRuleId())
                .code(rule.getCode())
                .passed(false)
                .message("Regla no implementada: " + rule.getCode())
                .severity(rule.getSeverity())
                .build();
    }

    public static RuleResult label(Rule rule, String label) {
        return RuleResult.builder()
                .ruleId(rule.getRuleId())
                .code(rule.getCode())
                .severity(rule.getSeverity())
                .label(label)
                .build();
    }
}
