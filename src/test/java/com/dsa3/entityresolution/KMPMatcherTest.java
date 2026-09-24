package com.dsa3.entityresolution;

import com.dsa3.entityresolution.algorithms.string.KMPMatcher;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KMPMatcherTest {

    @Test
    public void testComputeLPSArray() {
        int[] lps = KMPMatcher.computeLPSArray("AAAA");
        assertArrayEquals(new int[]{0, 1, 2, 3}, lps);

        int[] lps2 = KMPMatcher.computeLPSArray("ABCDE");
        assertArrayEquals(new int[]{0, 0, 0, 0, 0}, lps2);
    }

    @Test
    public void testSearchExactPattern() {
        String text = "apple iphone 15 128gb black";
        String pattern = "iphone 15";

        List<Integer> matches = KMPMatcher.search(text, pattern);
        assertEquals(1, matches.size());
        assertEquals(6, matches.get(0));
    }

    @Test
    public void testSearchNoMatch() {
        String text = "samsung galaxy s24";
        String pattern = "iphone";

        List<Integer> matches = KMPMatcher.search(text, pattern);
        assertTrue(matches.isEmpty());
    }

    @Test
    public void testTitleMatchScore() {
        double score = KMPMatcher.calculateTitleMatchScore("Apple iPhone 15 128GB Black", "iPhone 15 Apple 128GB Black");
        assertTrue(score > 0.7);
    }
}
