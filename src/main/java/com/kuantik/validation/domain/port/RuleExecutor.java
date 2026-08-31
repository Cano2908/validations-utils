package com.kuantik.validation.domain.port;

import com.kuantik.validation.domain.model.Rule;
import com.kuantik.validation.domain.model.RuleResult;
import org.w3c.dom.Document;

public interface RuleExecutor {
    String getCode();

    default String getSeverity() {
        return "ERROR";
    }

    RuleResult execute(Rule rule, Document xml);
}
