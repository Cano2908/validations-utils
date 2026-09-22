package com.kuantik.validation.infrastructure.blacklist;

import com.kuantik.validation.infrastructure.blacklist.dto.*;
import com.kuantik.validation.infrastructure.blacklist.enums.SatArticle;
import com.kuantik.validation.infrastructure.blacklist.exception.SatArticleUnavailableException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Evaluates whether an RFC appears in the SAT blacklists by CFF article.
 * Source of truth is {@code _meta.articulos} from the provider response:
 * {@code found > 0} means the RFC belongs to that article.
 * {@code matchingCollections} provides per-collection detail for audit purposes.
 * Semantics: {@code true} means risk (RFC found in a blacklist).
 */
@Component
@ConditionalOnProperty(name = "blacklist.base-url")
@RequiredArgsConstructor
public class SatBlacklistValidator {

    /**
     * 69-B collection name and DOF date field names used to determine contributor status.
     * Reliability note from provider: trust DOF dates over "Situación del contribuyente"
     * label — some records have mismatched labels but correct DOF dates.
     */
    static final String COLLECTION_69B = "sat_listado_completo";
    static final String KEY_DOF_PRESUNTOS = "Publicación DOF presuntos";
    static final String KEY_DOF_DEFINITIVOS = "Publicación DOF definitivos";
    static final String KEY_DOF_DESVIRTUADOS = "Publicación DOF desvirtuados";
    static final String KEY_DOF_SENTENCIA_FAVORABLE = "Publicación DOF sentencia favorable";

    private final BlacklistClient blacklistClient;

    // ── Article 69-B status-aware check (uses /rfc/detalle) ─────────────────

    /**
     * A record is at risk when it has a DOF publication date as Presunto or Definitivo
     * and has NOT been cleared via Desvirtuado or Sentencia favorable.
     */
    static boolean recordIsAtRisk(Map<String, Object> record) {
        boolean presunto = record.get(KEY_DOF_PRESUNTOS) != null;
        boolean definitivo = record.get(KEY_DOF_DEFINITIVOS) != null;
        boolean cleared = record.get(KEY_DOF_DESVIRTUADOS) != null
                || record.get(KEY_DOF_SENTENCIA_FAVORABLE) != null;
        return (presunto || definitivo) && !cleared;
    }

    // ── Generic article checks (uses /rfc/existe) ────────────────────────────

    private static ArticleStats resolveStats(ArticlesStats articles, SatArticle article) {
        return switch (article) {
            case ARTICLE_69 -> articles.article69();
            case ARTICLE_69B -> articles.article69b();
            case ARTICLE_69B_BIS -> articles.article69bBis();
        };
    }

    /**
     * Returns {@code true} if the RFC is at risk in Article 69-B — meaning it appears
     * as Presunto or Definitivo (confirmed by DOF publication date) and has NOT been
     * cleared (Desvirtuado or Sentencia favorable).
     * Uses the detail endpoint for accurate status determination.
     * DOF dates are used as the authoritative source, not the "Situación" label.
     */
    public boolean is69BAtRisk(String rfc) {
        BlacklistRfcDetailResponse response = blacklistClient.verifyRfcDetail(rfc);
        if (!response.success() || response.results().isEmpty()) {
            return false;
        }
        RfcDetailResult result = response.results().getFirst();
        if (!result.isFound() || result.getMatches() == null) {
            return false;
        }
        return result.getMatches().stream()
                .filter(m -> COLLECTION_69B.equals(m.getCollection()))
                .filter(m -> m.getRecords() != null)
                .flatMap(m -> m.getRecords().stream())
                .anyMatch(SatBlacklistValidator::recordIsAtRisk);
    }

    /**
     * Queries the service and returns {@code true} if the RFC appears in any of the given articles.
     * When no articles are provided, all three (69, 69-B, 69-B Bis) are evaluated.
     */
    public boolean isBlacklisted(String rfc, SatArticle... articles) {
        BlacklistRfcExistsResponse response = blacklistClient.verifyRfcExistence(rfc);
        if (!response.success() || response.results().isEmpty()) {
            return false;
        }
        return isBlacklisted(response.results().getFirst(), articles);
    }

    /**
     * Returns {@code true} if the RFC was found in any collection of the given article.
     *
     * @throws SatArticleUnavailableException if the provider did not return data for that article
     */
    public boolean hasMatchInArticle(RfcResult result, SatArticle article) {
        if (result.getMeta() == null || result.getMeta().articles() == null) {
            throw new SatArticleUnavailableException(article);
        }
        ArticleStats stats = resolveStats(result.getMeta().articles(), article);
        if (stats == null) {
            throw new SatArticleUnavailableException(article);
        }
        return stats.found() > 0;
    }

    /**
     * Returns the articles (from the provided subset) in which the RFC has a match.
     */
    public List<SatArticle> matchingArticles(RfcResult result, SatArticle... articles) {
        return Arrays.stream(articles)
                .filter(a -> hasMatchInArticle(result, a))
                .toList();
    }

    // ── private helpers ──────────────────────────────────────────────────────

    /**
     * Returns {@code true} if the RFC appears in any of the given articles.
     * When no articles are provided, all three are evaluated.
     */
    public boolean isBlacklisted(RfcResult result, SatArticle... articles) {
        SatArticle[] target = articles.length > 0 ? articles : SatArticle.values();
        return Arrays.stream(target)
                .anyMatch(a -> hasMatchInArticle(result, a));
    }

    /**
     * Returns the names of the specific collections that triggered a match for the given article.
     * Useful for audit/traceability; not needed for the validation itself.
     */
    public List<String> matchingCollections(RfcResult result, SatArticle article) {
        Map<String, String> byCollection = result.getArticleByCollection();
        Map<String, Boolean> collections = result.getCollections();

        if (byCollection == null || collections.isEmpty()) {
            return List.of();
        }

        return byCollection.entrySet().stream()
                .filter(e -> article.getCollectionLabel().equals(e.getValue()))
                .map(Map.Entry::getKey)
                .filter(col -> Boolean.TRUE.equals(collections.get(col)))
                .toList();
    }
}
