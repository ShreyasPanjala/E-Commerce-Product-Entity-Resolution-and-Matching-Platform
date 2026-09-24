package com.dsa3.entityresolution.algorithms.string;

import java.util.ArrayList;
import java.util.List;

/**
 * Knuth-Morris-Pratt (KMP) Pattern Matching Algorithm Implementation.
 * 
 * Time Complexity:
 * - Precomputation (LPS Array): O(M) where M is pattern length.
 * - Pattern Search: O(N) where N is text length.
 * Total Time Complexity: O(N + M)
 * Space Complexity: O(M) for the Longest Prefix Suffix (LPS) table.
 */
public class KMPMatcher {

    /**
     * Computes the Longest Prefix Suffix (LPS) / pi array for the pattern.
     * lps[i] stores the length of the longest proper prefix of pattern[0..i]
     * that is also a suffix of pattern[0..i].
     */
    public static int[] computeLPSArray(String pattern) {
        int m = pattern.length();
        int[] lps = new int[m];
        int len = 0; // length of the previous longest prefix suffix
        int i = 1;

        lps[0] = 0; // lps[0] is always 0

        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1]; // fallback without incrementing i
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        return lps;
    }

    /**
     * Searches for all occurrences of pattern in text using KMP algorithm.
     * @return List of 0-indexed starting positions of matches in text.
     */
    public static List<Integer> search(String text, String pattern) {
        List<Integer> matches = new ArrayList<>();
        if (text == null || pattern == null || pattern.isEmpty() || text.length() < pattern.length()) {
            return matches;
        }

        int n = text.length();
        int m = pattern.length();
        int[] lps = computeLPSArray(pattern);

        int i = 0; // index for text
        int j = 0; // index for pattern

        while (i < n) {
            if (pattern.charAt(j) == text.charAt(i)) {
                i++;
                j++;
            }

            if (j == m) {
                matches.add(i - j); // Match found at starting index (i - j)
                j = lps[j - 1];
            } else if (i < n && pattern.charAt(j) != text.charAt(i)) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }
        return matches;
    }

    /**
     * Checks if pattern exists anywhere within text.
     */
    public static boolean contains(String text, String pattern) {
        return !search(text, pattern).isEmpty();
    }

    /**
     * Calculates the degree of substring match coverage between pattern and text.
     * Returns a score between 0.0 and 1.0 based on exact token/pattern matches.
     */
    public static double calculateTitleMatchScore(String titleA, String titleB) {
        if (titleA == null || titleB == null || titleA.isEmpty() || titleB.isEmpty()) {
            return 0.0;
        }
        String lowerA = titleA.toLowerCase();
        String lowerB = titleB.toLowerCase();

        if (lowerA.equals(lowerB)) {
            return 1.0;
        }

        // Search shorter in longer
        String shorter = lowerA.length() <= lowerB.length() ? lowerA : lowerB;
        String longer = lowerA.length() <= lowerB.length() ? lowerB : lowerA;

        if (contains(longer, shorter)) {
            return (double) shorter.length() / longer.length();
        }

        // Token-level KMP matching
        String[] tokens = shorter.split("\\s+");
        int matchedChars = 0;
        int totalChars = 0;

        for (String token : tokens) {
            if (token.length() >= 3) {
                totalChars += token.length();
                if (contains(longer, token)) {
                    matchedChars += token.length();
                }
            }
        }

        return totalChars > 0 ? (double) matchedChars / totalChars : 0.0;
    }
}
