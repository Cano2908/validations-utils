package com.kuantik.validation.application;

import com.kuantik.validation.domain.exception.XmlParseException;
import com.kuantik.validation.domain.model.Rule;
import com.kuantik.validation.domain.model.RuleResult;
import com.kuantik.validation.domain.model.ValidationJob;
import com.kuantik.validation.domain.model.ValidationResult;
import com.kuantik.validation.domain.port.RuleExecutor;
import com.kuantik.validation.infrastructure.xml.SecureXmlParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
@Service
public class ValidateXmlUseCase {

    private final Map<String, RuleExecutor> ruleExecutors;
    private final SecureXmlParser secureXmlParser;

    public ValidateXmlUseCase(Map<String, RuleExecutor> ruleExecutors, SecureXmlParser secureXmlParser) {
        this.ruleExecutors = ruleExecutors;
        this.secureXmlParser = secureXmlParser;
    }

    public ValidationResult validate(ValidationJob validationJob) {
        Document document;
        try {
            document = this.secureXmlParser.parse(validationJob.getXmlContent());
        } catch (XmlParseException xmlParseException) {
            log.error("Error al parsear XML para job={}: {}", validationJob.getJobId(), xmlParseException.getMessage(), xmlParseException);
            return ValidationResult.failed(xmlParseException.getMessage());
        }

        List<Rule> rules = this.buildRules();
        log.info("Iniciando validación de job={} con {} reglas", validationJob.getJobId(), rules.size());
        List<RuleResult> ruleResults = new ArrayList<>();

        for (Rule rule : rules) {
            log.debug("Evaluando regla {} - job={} severidad={}", rule.getCode(), validationJob.getJobId(), rule.getSeverity());
            RuleExecutor executor = this.ruleExecutors.get(rule.getCode());

            RuleResult ruleResult = executor.execute(rule, document);
            log.debug("Regla {} - job={} resultado={} mensaje={}", rule.getCode(), validationJob.getJobId(), ruleResult.isPassed(), ruleResult.getMessage());
            ruleResults.add(ruleResult);
        }

        return ValidationResult.completed(ruleResults);
    }

    private List<Rule> buildRules() {
        return new TreeMap<>(this.ruleExecutors).entrySet().stream()
                .map(entry -> Rule.builder()
                        .ruleId(entry.getKey())
                        .code(entry.getKey())
                        .name(entry.getKey())
                        .severity(entry.getValue().getSeverity())
                        .build())
                .toList();
    }
}
