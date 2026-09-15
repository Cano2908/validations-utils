package com.kuantik.validation.infrastructure.blacklist.enums;

import lombok.Getter;

/**
 * CFF articles supported by the blacklist endpoint.
 * Article 69-B Bis (improper transfer of fiscal losses) is independent of 69-B (EFOS).
 * <p>
 * Article 49 Bis is intentionally excluded: the current provider has no collection for it.
 */
@Getter
public enum SatArticle {

    ARTICLE_69("articulo_69", "69"),
    ARTICLE_69B("articulo_69b", "69-B"),
    ARTICLE_69B_BIS("articulo_69b_bis", "69-B Bis");

    /**
     * Key inside {@code _meta.articulos} in the provider response.
     */
    private final String metaKey;

    /**
     * Value that appears in {@code articuloPorColeccion} for collections of this article.
     */
    private final String collectionLabel;

    SatArticle(String metaKey, String collectionLabel) {
        this.metaKey = metaKey;
        this.collectionLabel = collectionLabel;
    }

}
