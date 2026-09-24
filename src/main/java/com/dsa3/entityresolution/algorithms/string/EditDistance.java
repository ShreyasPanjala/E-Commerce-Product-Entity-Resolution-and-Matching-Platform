package com.dsa3.entityresolution.algorithms.string;

/**
 * Wagner-Fischer Dynamic Programming Edit Distance (Levenshtein Distance) Implementation.
 * 
 * Time Complexity: O(M * N) where M and N are lengths of the input strings.
 * Space Complexity: O(M * N) for DP matrix or O(min(M, N)) optimized space.
 */
public class EditDistance {

    /**
     * Computes raw Levenshtein edit distance between str1 and str2 using Wagner-Fischer DP algorithm.
     */
    public static int computeDistance(String str1, String str2) {
        if (str1 == null || str2 == null) {
            if (str1 == null && str2 == null) return 0;
            return str1 == null ? str2.length() : str1.length();
        }

        int m = str1.length();
        int n = str2.length();

        // dp[i][j] stores Levenshtein distance between str1[0..i-1] and str2[0..j-1]
        int[][] dp = new int[m + 1][n + 1];

        // Base cases: transforming to/from empty string
        for (int i = 0; i <= m; i++) {
            dp[i][0] = i; // Deletions
        }
        for (int j = 0; j <= n; j++) {
            dp[0][j] = j; // Insertions
        }

        // Fill DP table
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1]; // No edit operation required
                } else {
                    int substitution = dp[i - 1][j - 1] + 1;
                    int insertion = dp[i][j - 1] + 1;
                    int deletion = dp[i - 1][j] + 1;
                    dp[i][j] = Math.min(substitution, Math.min(insertion, deletion));
                }
            }
        }

        return dp[m][n];
    }

    /**
     * Computes normalized edit distance similarity score between 0.0 and 1.0.
     * Score = 1.0 - (editDistance / max(length1, length2))
     */
    public static double computeSimilarity(String str1, String str2) {
        if (str1 == null || str2 == null) {
            return (str1 == null && str2 == null) ? 1.0 : 0.0;
        }
        if (str1.equals(str2)) {
            return 1.0;
        }

        int dist = computeDistance(str1.toLowerCase(), str2.toLowerCase());
        int maxLength = Math.max(str1.length(), str2.length());

        if (maxLength == 0) {
            return 1.0;
        }

        return Math.max(0.0, 1.0 - ((double) dist / maxLength));
    }
}
