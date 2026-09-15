package com.kuantik.validation.infrastructure.blacklist.exception;

import com.kuantik.validation.infrastructure.blacklist.enums.SatArticle;

public class SatArticleUnavailableException extends RuntimeException {

    public SatArticleUnavailableException(SatArticle article) {
        super("Article %s is not available in the provider response".formatted(article));
    }
}
