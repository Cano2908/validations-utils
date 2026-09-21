package com.kuantik.validation.application;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuantik.validation.domain.model.Rule;
import com.kuantik.validation.domain.model.RuleResult;
import com.kuantik.validation.domain.model.ValidationJob;
import com.kuantik.validation.domain.model.ValidationResult;
import com.kuantik.validation.domain.port.JsonRuleExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
@Service
public class ValidateJsonUseCase {

    private final Map<String, JsonRuleExecutor> jsonRuleExecutors;
    private final ObjectMapper objectMapper;

    public ValidateJsonUseCase(Map<String, JsonRuleExecutor> jsonRuleExecutors, ObjectMapper objectMapper) {
        this.jsonRuleExecutors = jsonRuleExecutors;
        this.objectMapper = objectMapper;
    }

    public ValidationResult validate(ValidationJob validationJob) {
        Map<String, Object> data;
        try {
            data = this.objectMapper.readValue(
                    validationJob.getJsonContent(),
                    new TypeReference<Map<String, Object>>() {
                    });
        } catch (Exception parseException) {
            log.error("Error al parsear JSON para job={}: {}", validationJob.getJobId(), parseException.getMessage());
            return ValidationResult.failed("JSON inválido: " + parseException.getMessage());
        }

        List<Rule> rules = this.buildRules(validationJob.getRuleCodes());
        log.info("Iniciando validación JSON de job={} con {} reglas", validationJob.getJobId(), rules.size());
        List<RuleResult> ruleResults = new ArrayList<>();

        for (Rule rule : rules) {
            log.debug("Evaluando regla {} - job={} severidad={}", rule.getCode(), validationJob.getJobId(), rule.getSeverity());
            JsonRuleExecutor executor = this.jsonRuleExecutors.get(rule.getCode());

            RuleResult ruleResult;
            try {
                ruleResult = executor.execute(rule, data);
            } catch (Exception e) {
                log.error("Error inesperado en regla {} - job={}: {}", rule.getCode(), validationJob.getJobId(), e.getMessage(), e);
                ruleResult = RuleResult.failure(rule, "Error interno en regla " + rule.getCode() + ": " + e.getMessage());
            }
            log.debug("Regla {} - job={} resultado={} mensaje={}", rule.getCode(), validationJob.getJobId(), ruleResult.isPassed(), ruleResult.getMessage());
            ruleResults.add(ruleResult);
        }

        return ValidationResult.completed(ruleResults);
    }

    private List<Rule> buildRules(List<String> ruleCodes) {
        return new TreeMap<>(this.jsonRuleExecutors).entrySet().stream()
                .filter(entry -> ruleCodes == null || ruleCodes.isEmpty() || ruleCodes.contains(entry.getKey()))
                .map(entry -> Rule.builder()
                        .ruleId(entry.getKey())
                        .code(entry.getKey())
                        .name(entry.getKey())
                        .severity(entry.getValue().getSeverity())
                        .build())
                .toList();
    }
}
