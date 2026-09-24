package com.dsa3.entityresolution.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores matching comparison results between two product listings or a listing and a canonical product.
 */
public class MatchResult {
    private final ProductListing listingA;
    private final ProductListing listingB;
    private final CanonicalProduct canonicalProduct;
    private final double similarityScore;
    private final List<String> matchedByAlgorithms;
    private final String explanation;

    public MatchResult(ProductListing listingA, ProductListing listingB, double similarityScore,
                       List<String> matchedByAlgorithms, String explanation) {
        this.listingA = listingA;
        this.listingB = listingB;
        this.canonicalProduct = null;
        this.similarityScore = similarityScore;
        this.matchedByAlgorithms = matchedByAlgorithms != null ? matchedByAlgorithms : new ArrayList<>();
        this.explanation = explanation;
    }

    public MatchResult(ProductListing listingA, CanonicalProduct canonicalProduct, double similarityScore,
                       List<String> matchedByAlgorithms, String explanation) {
        this.listingA = listingA;
        this.listingB = null;
        this.canonicalProduct = canonicalProduct;
        this.similarityScore = similarityScore;
        this.matchedByAlgorithms = matchedByAlgorithms != null ? matchedByAlgorithms : new ArrayList<>();
        this.explanation = explanation;
    }

    public ProductListing getListingA() {
        return listingA;
    }

    public ProductListing getListingB() {
        return listingB;
    }

    public CanonicalProduct getCanonicalProduct() {
        return canonicalProduct;
    }

    public double getSimilarityScore() {
        return similarityScore;
    }

    public List<String> getMatchedByAlgorithms() {
        return matchedByAlgorithms;
    }

    public String getExplanation() {
        return explanation;
    }

    @Override
    public String toString() {
        String otherStr = listingB != null ? listingB.getListingId() :
                (canonicalProduct != null ? canonicalProduct.getCanonicalId() : "N/A");
        return String.format("Match: %s <-> %s | Score: %.3f | Algos: %s | %s",
                listingA.getListingId(), otherStr, similarityScore, String.join(", ", matchedByAlgorithms), explanation);
    }
}
