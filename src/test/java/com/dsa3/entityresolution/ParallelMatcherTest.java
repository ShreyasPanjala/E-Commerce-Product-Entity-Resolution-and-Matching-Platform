package com.dsa3.entityresolution;

import com.dsa3.entityresolution.algorithms.hashing.BlockingIndex;
import com.dsa3.entityresolution.algorithms.hashing.BlockingIndex.ListingPair;
import com.dsa3.entityresolution.data.CsvProductLoader;
import com.dsa3.entityresolution.data.SampleDataGenerator;
import com.dsa3.entityresolution.model.MatchResult;
import com.dsa3.entityresolution.model.ProductListing;
import com.dsa3.entityresolution.parallel.ParallelMatcher;
import com.dsa3.entityresolution.util.Constants;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParallelMatcherTest {

    @BeforeAll
    public static void setupData() throws IOException {
        SampleDataGenerator.generateSampleDatasets(Constants.DEFAULT_SAMPLE_CSV, Constants.DEFAULT_LARGER_CSV);
    }

    @Test
    public void testSequentialVsParallelResultsEquivalence() throws IOException {
        List<ProductListing> listings = CsvProductLoader.loadFromCsv(Constants.DEFAULT_SAMPLE_CSV);
        BlockingIndex index = new BlockingIndex();
        index.indexListings(listings);
        List<ListingPair> candidatePairs = index.getCandidatePairs();

        ParallelMatcher matcher = new ParallelMatcher();
        List<MatchResult> seqResults = matcher.evaluateSequential(candidatePairs);
        List<MatchResult> parResults = matcher.evaluateParallel(candidatePairs);

        assertEquals(seqResults.size(), parResults.size());
        for (int i = 0; i < seqResults.size(); i++) {
            assertEquals(seqResults.get(i).getSimilarityScore(), parResults.get(i).getSimilarityScore(), 1e-6);
        }
    }
}
