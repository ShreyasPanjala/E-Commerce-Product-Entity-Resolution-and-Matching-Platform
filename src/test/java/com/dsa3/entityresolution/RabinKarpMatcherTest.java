package com.dsa3.entityresolution;

import com.dsa3.entityresolution.algorithms.string.RabinKarpMatcher;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RabinKarpMatcherTest {

    @Test
    public void testRabinKarpPatternSearch() {
        RabinKarpMatcher rk = new RabinKarpMatcher();
        String text = "sony noise cancelling headphones wh1000xm5";
        String pattern = "wh1000xm5";

        List<Integer> matches = rk.search(text, pattern);
        assertEquals(1, matches.size());
        assertEquals(33, matches.get(0));
    }

    @Test
    public void testFragmentOverlapScore() {
        RabinKarpMatcher rk = new RabinKarpMatcher();
        String titleA = "samsung galaxy s24 256gb";
        String titleB = "galaxy s24 samsung 256gb smartphone";

        double score = rk.computeFragmentOverlapScore(titleA, titleB, 4);
        assertTrue(score > 0.4);
    }
}
