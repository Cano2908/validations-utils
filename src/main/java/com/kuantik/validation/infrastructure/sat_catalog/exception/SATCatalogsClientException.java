package com.kuantik.validation.infrastructure.sat_catalog.exception;

public class SATCatalogsClientException extends RuntimeException {

    public SATCatalogsClientException(String message) {
        super(message);
    }

    public SATCatalogsClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
