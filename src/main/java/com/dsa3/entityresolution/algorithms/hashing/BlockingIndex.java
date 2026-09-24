package com.dsa3.entityresolution.algorithms.hashing;

import com.dsa3.entityresolution.model.ProductListing;
import com.dsa3.entityresolution.preprocessing.Tokenizer;
import com.dsa3.entityresolution.util.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Candidate Blocking Indexing Engine to avoid O(N^2) exhaustive pair comparisons.
 * 
 * Partitions product listings into indexing buckets based on:
 * 1. Normalized Brand + Category keys
 * 2. Randomized Polynomial Hash buckets of word shingles/tokens
 */
public class BlockingIndex {

    private final Map<String, List<ProductListing>> blocks;
    private final RandomizedHasher hasher;

    public BlockingIndex() {
        this.blocks = new HashMap<>();
        this.hasher = new RandomizedHasher(42L); // Fixed seed for reproducible blocking
    }

    /**
     * Builds candidate blocking index for a list of product listings.
     */
    public void indexListings(List<ProductListing> listings) {
        blocks.clear();
        for (ProductListing listing : listings) {
            Set<String> keys = generateBlockingKeys(listing);
            for (String key : keys) {
                blocks.computeIfAbsent(key, k -> new ArrayList<>()).add(listing);
            }
        }
    }

    /**
     * Generates a set of candidate blocking keys for a single listing.
     */
    private Set<String> generateBlockingKeys(ProductListing listing) {
        Set<String> keys = new HashSet<>();

        String brand = listing.getBrand() != null ? listing.getBrand().toLowerCase().trim() : "generic";
        String category = listing.getCategory() != null ? listing.getCategory().toLowerCase().trim() : "general";

        // Primary block key: Brand + Category
        keys.add(brand + ":" + category);

        // Secondary block key: Brand + Hash bucket of title tokens
        List<String> tokens = Tokenizer.tokenizeWords(listing.getNormalizedTitle());
        for (String token : tokens) {
            if (token.length() >= 4) {
                int bucket = hasher.hash(token, Constants.NUM_HASH_BUCKETS);
                keys.add(brand + ":bucket_" + bucket);
                keys.add("cat_" + category + ":bucket_" + bucket);
            }
        }

        return keys;
    }

    /**
     * Retrieves candidate pairs sharing at least one blocking key bucket.
     */
    public List<ListingPair> getCandidatePairs() {
        Set<String> seenPairs = new HashSet<>();
        List<ListingPair> candidatePairs = new ArrayList<>();

        for (List<ProductListing> bucket : blocks.values()) {
            if (bucket.size() > 1) {
                for (int i = 0; i < bucket.size(); i++) {
                    for (int j = i + 1; j < bucket.size(); j++) {
                        ProductListing a = bucket.get(i);
                        ProductListing b = bucket.get(j);

                        // Ensure consistent pair order ID-wise
                        String pairId = a.getListingId().compareTo(b.getListingId()) < 0 ?
                                a.getListingId() + "<->" + b.getListingId() :
                                b.getListingId() + "<->" + a.getListingId();

                        if (!seenPairs.contains(pairId)) {
                            seenPairs.add(pairId);
                            candidatePairs.add(new ListingPair(a, b));
                        }
                    }
                }
            }
        }
        return candidatePairs;
    }

    public Map<String, List<ProductListing>> getBlocks() {
        return blocks;
    }

    /**
     * Helper record/class representing a candidate listing pair.
     */
    public static class ListingPair {
        private final ProductListing listingA;
        private final ProductListing listingB;

        public ListingPair(ProductListing listingA, ProductListing listingB) {
            this.listingA = listingA;
            this.listingB = listingB;
        }

        public ProductListing getListingA() {
            return listingA;
        }

        public ProductListing getListingB() {
            return listingB;
        }
    }
}
