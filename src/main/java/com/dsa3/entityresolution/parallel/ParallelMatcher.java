package com.dsa3.entityresolution.parallel;

import com.dsa3.entityresolution.algorithms.hashing.BlockingIndex.ListingPair;
import com.dsa3.entityresolution.algorithms.matching.SimilarityScorer;
import com.dsa3.entityresolution.model.MatchResult;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Parallel Processing Engine for scalable multi-threaded candidate pair matching.
 */
public class ParallelMatcher {

    private final SimilarityScorer scorer;

    public ParallelMatcher() {
        this.scorer = new SimilarityScorer();
    }

    /**
     * Evaluates candidate pairs sequentially on a single thread.
     */
    public List<MatchResult> evaluateSequential(List<ListingPair> pairs) {
        List<MatchResult> results = new ArrayList<>(pairs.size());
        for (ListingPair pair : pairs) {
            results.add(scorer.evaluatePair(pair.getListingA(), pair.getListingB()));
        }
        return results;
    }

    /**
     * Evaluates candidate pairs in parallel across available CPU cores.
     */
    public List<MatchResult> evaluateParallel(List<ListingPair> pairs) {
        return pairs.parallelStream()
                .map(pair -> scorer.evaluatePair(pair.getListingA(), pair.getListingB()))
                .collect(Collectors.toList());
    }

    /**
     * Benchmarks sequential vs parallel candidate pair evaluation latency.
     * @return Array containing [sequentialTimeMs, parallelTimeMs]
     */
    public long[] benchmarkPerformance(List<ListingPair> pairs) {
        // Warmup execution
        evaluateSequential(pairs.subList(0, Math.min(pairs.size(), 10)));

        // Sequential run
        long startSeq = System.currentTimeMillis();
        List<MatchResult> seqResults = evaluateSequential(pairs);
        long seqTimeMs = System.currentTimeMillis() - startSeq;

        // Parallel run
        long startPar = System.currentTimeMillis();
        List<MatchResult> parResults = evaluateParallel(pairs);
        long parTimeMs = System.currentTimeMillis() - startPar;

        return new long[]{seqTimeMs, parTimeMs};
    }
}
