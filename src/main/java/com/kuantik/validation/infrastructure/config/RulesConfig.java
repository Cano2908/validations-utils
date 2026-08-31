package com.kuantik.validation.infrastructure.config;

import com.kuantik.validation.domain.port.RuleExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@ComponentScan("com.kuantik.validation")
public class RulesConfig {

    @Bean
    public Map<String, RuleExecutor> ruleExecutors(List<RuleExecutor> ruleExecutorList) {
        return ruleExecutorList.stream()
                .collect(Collectors.toMap(
                        RuleExecutor::getCode,
                        Function.identity(),
                        (existing, duplicate) -> {
                            log.warn("Código de regla duplicado '{}' — se conserva: {}, se ignora: {}",
                                    existing.getCode(),
                                    existing.getClass().getSimpleName(),
                                    duplicate.getClass().getSimpleName());
                            return existing;
                        },
                        TreeMap::new));
    }
}
