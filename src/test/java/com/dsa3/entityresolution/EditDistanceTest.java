package com.dsa3.entityresolution;

import com.dsa3.entityresolution.algorithms.string.EditDistance;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EditDistanceTest {

    @Test
    public void testComputeDistanceIdentical() {
        int dist = EditDistance.computeDistance("Apple iPhone 15", "Apple iPhone 15");
        assertEquals(0, dist);
    }

    @Test
    public void testComputeDistanceSingleEdit() {
        int dist = EditDistance.computeDistance("iPhone 15", "iPhone 15s");
        assertEquals(1, dist);
    }

    @Test
    public void testComputeSimilarity() {
        double sim = EditDistance.computeSimilarity("Apple iPhone Fifteen 128GB Black", "Apple iPhone 15 128GB Black");
        assertTrue(sim > 0.7);
    }
}
