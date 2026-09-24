package com.dsa3.entityresolution.util;

/**
 * System-wide constants, parameters, threshold settings, and algorithm weights.
 */
public final class Constants {

    private Constants() {
        // Prevent instantiation
    }

    // Similarity Thresholds
    public static final double MATCH_THRESHOLD = 0.70;
    public static final double POSSIBLE_MATCH_THRESHOLD = 0.50;

    // Algorithm Weights for Composite Similarity Scoring (Sum = 1.0)
    public static final double WEIGHT_KMP = 0.25;
    public static final double WEIGHT_RABIN_KARP = 0.20;
    public static final double WEIGHT_EDIT_DISTANCE = 0.25;
    public static final double WEIGHT_SUFFIX_ARRAY = 0.15;
    public static final double WEIGHT_BRAND_CATEGORY = 0.10;
    public static final double WEIGHT_PRICE_SIMILARITY = 0.05;

    // Default Dataset File Paths
    public static final String DEFAULT_SAMPLE_CSV = "data/sample_products.csv";
    public static final String DEFAULT_LARGER_CSV = "data/larger_sample_products.csv";

    // Blocking / Hashing Parameters
    public static final int NUM_HASH_BUCKETS = 128;
    public static final int SHINGLE_SIZE = 3;

    // Rabin-Karp Polynomial Hash Settings
    public static final long RK_BASE = 256;
    public static final long RK_PRIME = 1000000007L;
}
