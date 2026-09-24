package com.dsa3.entityresolution.algorithms.matching;

import com.dsa3.entityresolution.model.CanonicalProduct;
import com.dsa3.entityresolution.model.MatchResult;
import com.dsa3.entityresolution.model.ProductListing;
import com.dsa3.entityresolution.util.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Greedy Approximation Clustering Matcher for scalable product entity resolution on large datasets.
 * 
 * Algorithm:
 * 1. Collect candidate listing pairs and sort descending by similarity score: O(E log E).
 * 2. Process candidate pairs iteratively from highest to lowest similarity.
 * 3. Use Disjoint Set Union (DSU / Union-Find) to merge listings into canonical product sets.
 * 
 * Time Complexity: O(E log E + E * alpha(V)) where E is number of candidate pairs.
 * Space Complexity: O(V + E) for DSU parent maps and candidate result lists.
 */
public class GreedyMatcher {

    private final double matchThreshold;

    public GreedyMatcher() {
        this(Constants.MATCH_THRESHOLD);
    }

    public GreedyMatcher(double matchThreshold) {
        this.matchThreshold = matchThreshold;
    }

    /**
     * Resolves candidate product listing pairs into canonical product clusters greedily.
     */
    public List<CanonicalProduct> groupListings(List<ProductListing> listings, List<MatchResult> matchResults) {
        // Disjoint Set Union (DSU) initialization
        Map<String, String> parent = new HashMap<>();
        Map<String, ProductListing> listingMap = new HashMap<>();

        for (ProductListing listing : listings) {
            parent.put(listing.getListingId(), listing.getListingId());
            listingMap.put(listing.getListingId(), listing);
        }

        // Sort candidate matches in descending order of similarity score
        List<MatchResult> sortedMatches = new ArrayList<>(matchResults);
        sortedMatches.sort((m1, m2) -> Double.compare(m2.getSimilarityScore(), m1.getSimilarityScore()));

        // Greedy DSU merge
        for (MatchResult match : sortedMatches) {
            if (match.getSimilarityScore() >= matchThreshold && match.getListingB() != null) {
                String rootA = find(parent, match.getListingA().getListingId());
                String rootB = find(parent, match.getListingB().getListingId());

                if (!rootA.equals(rootB)) {
                    parent.put(rootB, rootA); // Union clusters
                }
            }
        }

        // Group listings by root parent ID
        Map<String, List<ProductListing>> clusters = new HashMap<>();
        for (ProductListing listing : listings) {
            String root = find(parent, listing.getListingId());
            clusters.computeIfAbsent(root, k -> new ArrayList<>()).add(listing);
        }

        // Build CanonicalProduct objects from clusters
        List<CanonicalProduct> canonicalCatalog = new ArrayList<>();
        int idCounter = 1;
        for (List<ProductListing> cluster : clusters.values()) {
            String canonicalId = String.format("CP-%04d", idCounter++);
            CanonicalProduct cp = new CanonicalProduct(canonicalId, cluster.get(0));
            for (int i = 1; i < cluster.size(); i++) {
                cp.addListing(cluster.get(i));
            }
            canonicalCatalog.add(cp);
        }

        return canonicalCatalog;
    }

    private String find(Map<String, String> parent, String id) {
        if (!parent.get(id).equals(id)) {
            parent.put(id, find(parent, parent.get(id))); // Path compression
        }
        return parent.get(id);
    }
}
