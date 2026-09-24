package com.dsa3.entityresolution.algorithms.matching;

import com.dsa3.entityresolution.algorithms.string.EditDistance;
import com.dsa3.entityresolution.algorithms.string.KMPMatcher;
import com.dsa3.entityresolution.algorithms.string.RabinKarpMatcher;
import com.dsa3.entityresolution.algorithms.string.SuffixArray;
import com.dsa3.entityresolution.model.MatchResult;
import com.dsa3.entityresolution.model.ProductListing;
import com.dsa3.entityresolution.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Composite similarity scoring engine combining multi-algorithm string, structural, and price signals.
 */
public class SimilarityScorer {

    private final RabinKarpMatcher rkMatcher;

    public SimilarityScorer() {
        this.rkMatcher = new RabinKarpMatcher();
    }

    /**
     * Evaluates full similarity between two product listings across all algorithms.
     */
    public MatchResult evaluatePair(ProductListing a, ProductListing b) {
        List<String> matchedAlgos = new ArrayList<>();

        // 1. KMP Title Substring Match
        double kmpScore = KMPMatcher.calculateTitleMatchScore(a.getNormalizedTitle(), b.getNormalizedTitle());
        if (kmpScore >= 0.6) {
            matchedAlgos.add("KMP-Substring");
        }

        // 2. Rabin-Karp Rolling Hash Fragment Overlap
        int fragLen = Math.min(4, Math.min(a.getNormalizedTitle().length(), b.getNormalizedTitle().length()));
        double rkScore = fragLen >= 3 ?
                rkMatcher.computeFragmentOverlapScore(a.getNormalizedTitle(), b.getNormalizedTitle(), fragLen) : 0.0;
        if (rkScore >= 0.5) {
            matchedAlgos.add("RabinKarp-RollingHash");
        }

        // 3. Wagner-Fischer Edit Distance Title Similarity (Raw + Token-Sorted for Permutations)
        double rawEditScore = EditDistance.computeSimilarity(a.getNormalizedTitle(), b.getNormalizedTitle());
        double tokenSortedEditScore = EditDistance.computeSimilarity(sortTokens(a.getNormalizedTitle()), sortTokens(b.getNormalizedTitle()));
        double editScore = Math.max(rawEditScore, tokenSortedEditScore);
        if (editScore >= 0.7) {
            matchedAlgos.add("WagnerFischer-EditDistance");
        }

        // 4. Suffix Array Description Overlap
        double saScore = SuffixArray.calculateDescriptionSimilarity(
                a.getNormalizedDescription(), b.getNormalizedDescription());
        if (saScore >= 0.3) {
            matchedAlgos.add("SuffixArray-LCP");
        }

        // 5. Brand & Category Match Score
        double brandCatScore = calculateBrandCategoryScore(a, b);
        if (brandCatScore >= 0.9) {
            matchedAlgos.add("BrandCategory-ExactMatch");
        }

        // 6. Price Range Similarity Score
        double priceScore = calculatePriceSimilarity(a.getPrice(), b.getPrice());
        if (priceScore >= 0.8) {
            matchedAlgos.add("PriceRange-Similarity");
        }

        // Weighted Composite Score Calculation
        double compositeScore = (Constants.WEIGHT_KMP * kmpScore) +
                (Constants.WEIGHT_RABIN_KARP * rkScore) +
                (Constants.WEIGHT_EDIT_DISTANCE * editScore) +
                (Constants.WEIGHT_SUFFIX_ARRAY * saScore) +
                (Constants.WEIGHT_BRAND_CATEGORY * brandCatScore) +
                (Constants.WEIGHT_PRICE_SIMILARITY * priceScore);

        // Normalize between 0.0 and 1.0
        compositeScore = Math.max(0.0, Math.min(1.0, compositeScore));

        // Generate Human-Readable Explanation
        String explanation = generateExplanation(compositeScore, kmpScore, rkScore, editScore, saScore, brandCatScore, priceScore);

        return new MatchResult(a, b, compositeScore, matchedAlgos, explanation);
    }

    private double calculateBrandCategoryScore(ProductListing a, ProductListing b) {
        boolean brandMatch = a.getBrand() != null && b.getBrand() != null &&
                a.getBrand().equalsIgnoreCase(b.getBrand());
        boolean catMatch = a.getCategory() != null && b.getCategory() != null &&
                a.getCategory().equalsIgnoreCase(b.getCategory());

        if (brandMatch && catMatch) return 1.0;
        if (brandMatch) return 0.7;
        if (catMatch) return 0.4;
        return 0.0;
    }

    private double calculatePriceSimilarity(double p1, double p2) {
        if (p1 <= 0 || p2 <= 0) return 0.5;
        double minP = Math.min(p1, p2);
        double maxP = Math.max(p1, p2);
        return minP / maxP; // Ratio between 0.0 and 1.0
    }

    private String generateExplanation(double total, double kmp, double rk, double edit, double sa, double brand, double price) {
        if (total >= Constants.MATCH_THRESHOLD) {
            return String.format("CONFIRMED MATCH (Score: %.2f) - High title similarity (Edit: %.2f, KMP: %.2f) & brand alignment.",
                    total, edit, kmp);
        } else if (total >= Constants.POSSIBLE_MATCH_THRESHOLD) {
            return String.format("POSSIBLE MATCH (Score: %.2f) - Partial string similarity (RK: %.2f, Suffix: %.2f).",
                    total, rk, sa);
        } else {
            return String.format("NO MATCH (Score: %.2f) - Distinct product entities.", total);
        }
    }

    private String sortTokens(String str) {
        if (str == null || str.isEmpty()) return "";
        String[] tokens = str.split("\\s+");
        java.util.Arrays.sort(tokens);
        return String.join(" ", tokens);
    }
}
