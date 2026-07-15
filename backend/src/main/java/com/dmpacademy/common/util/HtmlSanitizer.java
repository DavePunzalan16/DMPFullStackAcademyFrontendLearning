package com.dmpacademy.common.util;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

/**
 * Sanitizes user-generated HTML content to prevent XSS attacks.
 * Allows basic formatting (bold, italic, links, paragraphs) while
 * stripping dangerous elements (script, event handlers, etc.).
 */
public final class HtmlSanitizer {

    private static final PolicyFactory POLICY = Sanitizers.FORMATTING
            .and(Sanitizers.LINKS)
            .and(Sanitizers.BLOCKS)
            .and(Sanitizers.TABLES);

    private HtmlSanitizer() {}

    /**
     * Sanitizes HTML content, removing dangerous tags/attributes.
     *
     * @param untrustedHtml the raw user input
     * @return sanitized safe HTML string
     */
    public static String sanitize(String untrustedHtml) {
        if (untrustedHtml == null || untrustedHtml.isBlank()) {
            return untrustedHtml;
        }
        return POLICY.sanitize(untrustedHtml);
    }
}
