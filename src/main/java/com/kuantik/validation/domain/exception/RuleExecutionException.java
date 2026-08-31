package com.kuantik.validation.domain.exception;

public class RuleExecutionException extends RuntimeException {
    public RuleExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
