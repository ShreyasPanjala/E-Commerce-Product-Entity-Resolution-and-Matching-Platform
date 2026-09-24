package com.dsa3.entityresolution;

import com.dsa3.entityresolution.algorithms.matching.GreedyMatcher;
import com.dsa3.entityresolution.model.CanonicalProduct;
import com.dsa3.entityresolution.model.MatchResult;
import com.dsa3.entityresolution.model.ProductListing;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GreedyMatcherTest {

    @Test
    public void testGroupObviousDuplicates() {
        ProductListing l1 = new ProductListing("L1", "S1", "StoreA", "Apple iPhone 15 128GB Black", "desc", "Apple", "Smartphones", 799.0, null);
        ProductListing l2 = new ProductListing("L2", "S2", "StoreB", "iPhone 15 Apple 128 GB - Black", "desc", "Apple", "Smartphones", 789.0, null);
        ProductListing l3 = new ProductListing("L3", "S3", "StoreC", "Samsung Galaxy S24 256GB", "desc", "Samsung", "Smartphones", 850.0, null);

        List<ProductListing> listings = List.of(l1, l2, l3);

        List<MatchResult> matches = new ArrayList<>();
        matches.add(new MatchResult(l1, l2, 0.88, List.of("KMP", "EditDistance"), "High similarity"));
        matches.add(new MatchResult(l1, l3, 0.20, List.of(), "No similarity"));

        GreedyMatcher matcher = new GreedyMatcher(0.75);
        List<CanonicalProduct> catalog = matcher.groupListings(listings, matches);

        assertEquals(2, catalog.size());
    }
}
