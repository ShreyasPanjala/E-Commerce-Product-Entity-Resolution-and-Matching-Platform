package com.dsa3.entityresolution.preprocessing;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility for tokenizing strings into words, character n-grams, and word shingles.
 */
public class Tokenizer {

    /**
     * Splits text into normalized word tokens.
     */
    public static List<String> tokenizeWords(String text) {
        List<String> tokens = new ArrayList<>();
        if (text == null || text.trim().isEmpty()) {
            return tokens;
        }
        String normalized = TextNormalizer.normalize(text);
        for (String word : normalized.split(" ")) {
            if (!word.isEmpty()) {
                tokens.add(word);
            }
        }
        return tokens;
    }

    /**
     * Extracts character N-grams from text.
     */
    public static List<String> extractCharacterNGrams(String text, int n) {
        List<String> nGrams = new ArrayList<>();
        if (text == null || text.length() < n) {
            return nGrams;
        }
        String cleaned = text.replaceAll("\\s+", " ").toLowerCase();
        for (int i = 0; i <= cleaned.length() - n; i++) {
            nGrams.add(cleaned.substring(i, i + n));
        }
        return nGrams;
    }

    /**
     * Extracts word shingles (sequences of k contiguous words).
     */
    public static List<String> extractWordShingles(String text, int k) {
        List<String> shingles = new ArrayList<>();
        List<String> words = tokenizeWords(text);
        if (words.size() < k) {
            if (!words.isEmpty()) {
                shingles.add(String.join(" ", words));
            }
            return shingles;
        }
        for (int i = 0; i <= words.size() - k; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < k; j++) {
                if (j > 0) sb.append(" ");
                sb.append(words.get(i + j));
            }
            shingles.add(sb.toString());
        }
        return shingles;
    }
}
