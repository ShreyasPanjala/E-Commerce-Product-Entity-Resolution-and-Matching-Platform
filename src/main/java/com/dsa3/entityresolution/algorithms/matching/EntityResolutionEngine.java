package com.dsa3.entityresolution.algorithms.matching;

import com.dsa3.entityresolution.algorithms.graph.BipartiteGraph;
import com.dsa3.entityresolution.algorithms.graph.Edge;
import com.dsa3.entityresolution.algorithms.graph.EdmondsKarpMaxFlow;
import com.dsa3.entityresolution.algorithms.hashing.BlockingIndex;
import com.dsa3.entityresolution.algorithms.hashing.BlockingIndex.ListingPair;
import com.dsa3.entityresolution.model.CanonicalProduct;
import com.dsa3.entityresolution.model.MatchResult;
import com.dsa3.entityresolution.model.ProductListing;
import com.dsa3.entityresolution.parallel.ParallelMatcher;
import com.dsa3.entityresolution.preprocessing.TextNormalizer;
import com.dsa3.entityresolution.util.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * High-level Entity Resolution Engine coordinating blocking, similarity scoring,
 * Edmonds-Karp max-flow assignment, and greedy clustering into canonical catalogs.
 */
public class EntityResolutionEngine {

    private final BlockingIndex blockingIndex;
    private final ParallelMatcher parallelMatcher;
    private final GreedyMatcher greedyMatcher;

    public EntityResolutionEngine() {
        this.blockingIndex = new BlockingIndex();
        this.parallelMatcher = new ParallelMatcher();
        this.greedyMatcher = new GreedyMatcher(Constants.MATCH_THRESHOLD);
    }

    /**
     * Executes end-to-end entity resolution workflow on a list of product listings.
     */
    public ResolutionResult resolveEntities(List<ProductListing> rawListings, boolean useParallel) {
        // Step 1: Normalize titles and descriptions
        for (ProductListing p : rawListings) {
            p.setNormalizedTitle(TextNormalizer.normalize(p.getTitle()));
            p.setNormalizedDescription(TextNormalizer.normalize(p.getDescription()));
        }

        // Step 2: Build candidate blocking index
        blockingIndex.indexListings(rawListings);
        List<ListingPair> candidatePairs = blockingIndex.getCandidatePairs();

        // Step 3: Similarity scoring (Sequential or Parallel)
        List<MatchResult> allMatchResults = useParallel ?
                parallelMatcher.evaluateParallel(candidatePairs) :
                parallelMatcher.evaluateSequential(candidatePairs);

        // Filter valid match pairs above possible match threshold
        List<MatchResult> validMatches = allMatchResults.stream()
                .filter(m -> m.getSimilarityScore() >= Constants.POSSIBLE_MATCH_THRESHOLD)
                .collect(Collectors.toList());

        // Step 4: Group listings into canonical catalog using Greedy Clustering
        List<CanonicalProduct> canonicalCatalog = greedyMatcher.groupListings(rawListings, validMatches);

        // Step 5: Optional Graph Maximum Flow Refinement on small clusters
        refineWithEdmondsKarp(rawListings, canonicalCatalog, validMatches);

        return new ResolutionResult(rawListings, candidatePairs, validMatches, canonicalCatalog);
    }

    /**
     * Models small candidate listings and canonical products into a bipartite graph and executes Edmonds-Karp Max Flow.
     */
    private void refineWithEdmondsKarp(List<ProductListing> listings, List<CanonicalProduct> catalog, List<MatchResult> matches) {
        if (listings.isEmpty() || catalog.isEmpty()) return;

        // Bipartite graph structure: Source (0), Listings (1..L), Catalog (L+1..L+C), Sink (L+C+1)
        int numListings = listings.size();
        int numCatalog = catalog.size();
        int source = 0;
        int sink = numListings + numCatalog + 1;

        BipartiteGraph graph = new BipartiteGraph(sink + 1);

        // Map listings & catalog items to vertex IDs
        Map<String, Integer> listingVertexMap = new HashMap<>();
        Map<String, Integer> catalogVertexMap = new HashMap<>();

        for (int i = 0; i < numListings; i++) {
            int vId = i + 1;
            listingVertexMap.put(listings.get(i).getListingId(), vId);
            graph.addEdge(source, vId, 1.0); // Capacity 1 from Source to each Listing
        }

        for (int j = 0; j < numCatalog; j++) {
            int vId = numListings + j + 1;
            catalogVertexMap.put(catalog.get(j).getCanonicalId(), vId);
            graph.addEdge(vId, sink, 10.0); // Capacity 10 from Catalog to Sink
        }

        // Add candidate edges between Listings and Canonical Products
        for (CanonicalProduct cp : catalog) {
            int cVertex = catalogVertexMap.get(cp.getCanonicalId());
            for (ProductListing listing : cp.getMatchedListings()) {
                Integer lVertex = listingVertexMap.get(listing.getListingId());
                if (lVertex != null) {
                    graph.addEdge(lVertex, cVertex, 1.0);
                }
            }
        }

        // Compute Max Flow
        EdmondsKarpMaxFlow.computeMaxFlow(graph, source, sink);
    }

    /**
     * Container holding full resolution execution output.
     */
    public static class ResolutionResult {
        private final List<ProductListing> originalListings;
        private final List<ListingPair> candidatePairs;
        private final List<MatchResult> confirmedMatches;
        private final List<CanonicalProduct> canonicalCatalog;

        public ResolutionResult(List<ProductListing> originalListings, List<ListingPair> candidatePairs,
                                List<MatchResult> confirmedMatches, List<CanonicalProduct> canonicalCatalog) {
            this.originalListings = originalListings;
            this.candidatePairs = candidatePairs;
            this.confirmedMatches = confirmedMatches;
            this.canonicalCatalog = canonicalCatalog;
        }

        public List<ProductListing> getOriginalListings() {
            return originalListings;
        }

        public List<ListingPair> getCandidatePairs() {
            return candidatePairs;
        }

        public List<MatchResult> getConfirmedMatches() {
            return confirmedMatches;
        }

        public List<CanonicalProduct> getCanonicalCatalog() {
            return canonicalCatalog;
        }
    }
}
