package com.dsa3.entityresolution.algorithms.string;

import java.util.Arrays;

/**
 * Suffix Array and Longest Common Prefix (LCP) Construction for Text Similarity Analysis.
 * 
 * Time Complexity:
 * - Suffix Array Construction: O(N log^2 N) or O(N log N) using custom suffix sorting.
 * - LCP Array Construction (Kasai's Algorithm): O(N)
 * - Longest Common Substring (LCS) Search: O(N) over LCP array.
 * Space Complexity: O(N) space for suffix and LCP arrays.
 */
public class SuffixArray {

    private final String text;
    private final int n;
    private final Integer[] suffixArray;
    private final int[] lcp;

    public SuffixArray(String text) {
        this.text = text;
        this.n = text.length();
        this.suffixArray = new Integer[n];
        this.lcp = new int[n];
        buildSuffixArray();
        buildLCPArray();
    }

    /**
     * Builds Suffix Array by lexicographically sorting all suffix starting indices.
     */
    private void buildSuffixArray() {
        for (int i = 0; i < n; i++) {
            suffixArray[i] = i;
        }

        Arrays.sort(suffixArray, (i1, i2) -> {
            String s1 = text.substring(i1);
            String s2 = text.substring(i2);
            return s1.compareTo(s2);
        });
    }

    /**
     * Builds Longest Common Prefix (LCP) array using Kasai's algorithm in O(N) time.
     */
    private void buildLCPArray() {
        int[] rank = new int[n];
        for (int i = 0; i < n; i++) {
            rank[suffixArray[i]] = i;
        }

        int h = 0;
        for (int i = 0; i < n; i++) {
            if (rank[i] > 0) {
                int j = suffixArray[rank[i] - 1];
                while (i + h < n && j + h < n && text.charAt(i + h) == text.charAt(j + h)) {
                    h++;
                }
                lcp[rank[i]] = h;
                if (h > 0) {
                    h--;
                }
            }
        }
    }

    public Integer[] getSuffixArray() {
        return suffixArray;
    }

    public int[] getLcp() {
        return lcp;
    }

    /**
     * Computes the Longest Common Substring between two texts using a combined Suffix Array.
     */
    public static String findLongestCommonSubstring(String text1, String text2) {
        if (text1 == null || text2 == null || text1.isEmpty() || text2.isEmpty()) {
            return "";
        }

        String separator = "#";
        String combined = text1 + separator + text2;
        int len1 = text1.length();

        SuffixArray sa = new SuffixArray(combined);
        Integer[] suffixArray = sa.getSuffixArray();
        int[] lcp = sa.getLcp();

        int maxLcp = 0;
        int maxIndex = -1;

        for (int i = 1; i < combined.length(); i++) {
            int idx1 = suffixArray[i - 1];
            int idx2 = suffixArray[i];

            // Check if one suffix starts in text1 and the other in text2
            boolean span1 = (idx1 < len1 && idx2 > len1);
            boolean span2 = (idx2 < len1 && idx1 > len1);

            if (span1 || span2) {
                if (lcp[i] > maxLcp) {
                    maxLcp = lcp[i];
                    maxIndex = suffixArray[i];
                }
            }
        }

        if (maxLcp > 0 && maxIndex != -1) {
            return combined.substring(maxIndex, maxIndex + maxLcp);
        }
        return "";
    }

    /**
     * Computes description similarity score based on shared common substrings relative to text length.
     */
    public static double calculateDescriptionSimilarity(String desc1, String desc2) {
        if (desc1 == null || desc2 == null || desc1.isEmpty() || desc2.isEmpty()) {
            return 0.0;
        }
        String lcs = findLongestCommonSubstring(desc1.toLowerCase(), desc2.toLowerCase());
        int maxLength = Math.max(desc1.length(), desc2.length());
        return (double) lcs.length() / maxLength;
    }
}
