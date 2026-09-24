package com.dsa3.entityresolution.algorithms.string;

import com.dsa3.entityresolution.util.Constants;
import java.util.ArrayList;
import java.util.List;

/**
 * Rabin-Karp Rolling Hash String Matching Algorithm Implementation.
 * 
 * Time Complexity:
 * - Precomputation: O(M) where M is pattern length.
 * - Average Search Time: O(N + M) where N is text length.
 * - Worst Case (Hash collisions): O(N * M)
 * Space Complexity: O(1) auxiliary memory.
 */
public class RabinKarpMatcher {

    private final long base;
    private final long prime;

    public RabinKarpMatcher() {
        this(Constants.RK_BASE, Constants.RK_PRIME);
    }

    public RabinKarpMatcher(long base, long prime) {
        this.base = base;
        this.prime = prime;
    }

    /**
     * Searches for all occurrences of pattern in text using Rabin-Karp rolling hash.
     * @return List of starting indices in text.
     */
    public List<Integer> search(String text, String pattern) {
        List<Integer> matches = new ArrayList<>();
        if (text == null || pattern == null || pattern.isEmpty() || text.length() < pattern.length()) {
            return matches;
        }

        int n = text.length();
        int m = pattern.length();

        // Calculate h = (base^(m-1)) % prime
        long h = 1;
        for (int i = 0; i < m - 1; i++) {
            h = (h * base) % prime;
        }

        long patternHash = 0;
        long textHash = 0;

        // Calculate initial hash values for pattern and first window of text
        for (int i = 0; i < m; i++) {
            patternHash = (base * patternHash + pattern.charAt(i)) % prime;
            textHash = (base * textHash + text.charAt(i)) % prime;
        }

        // Slide the pattern over text
        for (int i = 0; i <= n - m; i++) {
            // If hash values match, verify character by character to handle collisions
            if (patternHash == textHash) {
                boolean match = true;
                for (int j = 0; j < m; j++) {
                    if (text.charAt(i + j) != pattern.charAt(j)) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    matches.add(i);
                }
            }

            // Calculate rolling hash for next window of text
            if (i < n - m) {
                textHash = (base * (textHash - text.charAt(i) * h) + text.charAt(i + m)) % prime;
                // If textHash turns negative, convert to positive
                if (textHash < 0) {
                    textHash = (textHash + prime);
                }
            }
        }
        return matches;
    }

    /**
     * Computes fragment overlap score between two product titles using sliding window Rabin-Karp hashing.
     */
    public double computeFragmentOverlapScore(String textA, String textB, int fragmentLength) {
        if (textA == null || textB == null || textA.length() < fragmentLength || textB.length() < fragmentLength) {
            return 0.0;
        }

        int m = fragmentLength;
        int nA = textA.length();
        int nB = textB.length();

        // Collect all rolling hashes of textA fragments
        List<Long> hashesA = new ArrayList<>();
        long h = 1;
        for (int i = 0; i < m - 1; i++) {
            h = (h * base) % prime;
        }

        long currentHash = 0;
        for (int i = 0; i < m; i++) {
            currentHash = (base * currentHash + textA.charAt(i)) % prime;
        }
        hashesA.add(currentHash);

        for (int i = 0; i < nA - m; i++) {
            currentHash = (base * (currentHash - textA.charAt(i) * h) + textA.charAt(i + m)) % prime;
            if (currentHash < 0) currentHash += prime;
            hashesA.add(currentHash);
        }

        // Check textB fragments against hashesA
        int matchingFragments = 0;
        int totalFragmentsB = nB - m + 1;

        currentHash = 0;
        for (int i = 0; i < m; i++) {
            currentHash = (base * currentHash + textB.charAt(i)) % prime;
        }
        if (hashesA.contains(currentHash)) matchingFragments++;

        for (int i = 0; i < nB - m; i++) {
            currentHash = (base * (currentHash - textB.charAt(i) * h) + textB.charAt(i + m)) % prime;
            if (currentHash < 0) currentHash += prime;
            if (hashesA.contains(currentHash)) {
                matchingFragments++;
            }
        }

        return totalFragmentsB > 0 ? (double) matchingFragments / totalFragmentsB : 0.0;
    }
}
