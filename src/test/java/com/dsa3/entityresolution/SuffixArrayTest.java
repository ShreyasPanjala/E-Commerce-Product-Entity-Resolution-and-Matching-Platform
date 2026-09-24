package com.dsa3.entityresolution;

import com.dsa3.entityresolution.algorithms.string.SuffixArray;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SuffixArrayTest {

    @Test
    public void testLongestCommonSubstring() {
        String desc1 = "Industry leading noise cancelling wireless over ear headphones by sony.";
        String desc2 = "Premium sony wh1000xm5 wireless noise cancelling headset with micro microphones.";

        String lcs = SuffixArray.findLongestCommonSubstring(desc1, desc2);
        assertTrue(lcs.contains("noise cancelling"));
    }

    @Test
    public void testDescriptionSimilarity() {
        String desc1 = "Apple iPhone 15 smartphone 128GB Midnight Black.";
        String desc2 = "Apple iPhone 15 128GB Midnight Black edition.";

        double sim = SuffixArray.calculateDescriptionSimilarity(desc1, desc2);
        assertTrue(sim > 0.4);
    }
}
