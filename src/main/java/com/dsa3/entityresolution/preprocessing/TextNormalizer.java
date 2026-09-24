package com.dsa3.entityresolution.preprocessing;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Normalizes e-commerce title and description strings for noise-resilient matching.
 */
public class TextNormalizer {

    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "a", "an", "the", "and", "or", "for", "with", "by", "on", "at", "in", "to",
            "from", "of", "new", "buy", "sale", "best", "deal", "edition", "ver", "version", "original"
    ));

    /**
     * Normalizes input string: converts to lowercase, strips non-alphanumeric chars (except spaces),
     * reduces multiple spaces, and optionally removes filler stop words.
     */
    public static String normalize(String text, boolean removeStopWords) {
        if (text == null || text.trim().isEmpty()) {
            return "";
        }

        // 1. Lowercase
        String cleaned = text.toLowerCase();

        // 2. Replace non-alphanumeric characters with space
        cleaned = cleaned.replaceAll("[^a-z0-9\\s]", " ");

        // 3. Normalize digits and common unit spaces (e.g. "128 gb" -> "128gb")
        cleaned = cleaned.replaceAll("(\\d+)\\s+(gb|tb|mb|hz|khz|ghz|mp|mah|mm|inch|in|k)", "$1$2");

        // 4. Normalize multiple whitespace into single space
        cleaned = cleaned.replaceAll("\\s+", " ").trim();

        if (!removeStopWords) {
            return cleaned;
        }

        // 4. Filter stop words
        String[] tokens = cleaned.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String token : tokens) {
            if (!token.isEmpty() && !STOP_WORDS.contains(token)) {
                if (sb.length() > 0) {
                    sb.append(" ");
                }
                sb.append(token);
            }
        }
        return sb.toString();
    }

    /**
     * Standard normalization removing stop words by default.
     */
    public static String normalize(String text) {
        return normalize(text, true);
    }
}
