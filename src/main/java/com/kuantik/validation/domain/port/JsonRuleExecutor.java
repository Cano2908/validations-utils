package com.kuantik.validation.domain.port;

import com.kuantik.validation.domain.model.Rule;
import com.kuantik.validation.domain.model.RuleResult;

import java.util.Map;

public interface JsonRuleExecutor {
    String getCode();

    default String getSeverity() {
        return "ERROR";
    }

    RuleResult execute(Rule rule, Map<String, Object> data);
}
