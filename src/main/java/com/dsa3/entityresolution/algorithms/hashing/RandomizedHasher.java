package com.dsa3.entityresolution.algorithms.hashing;

import java.util.Random;

/**
 * Handcrafted Randomized Polynomial Hashing Algorithm for String & Token MinHash signatures.
 * 
 * Time Complexity: O(L) per string of length L.
 * Space Complexity: O(1) auxiliary memory.
 */
public class RandomizedHasher {

    private final long a;
    private final long b;
    private final long prime;

    public RandomizedHasher() {
        this(new Random().nextLong());
    }

    public RandomizedHasher(long seed) {
        Random rand = new Random(seed);
        this.prime = 2147483647L; // Mersenne Prime 2^31 - 1
        this.a = Math.abs(rand.nextLong()) % (prime - 1) + 1;
        this.b = Math.abs(rand.nextLong()) % prime;
    }

    public RandomizedHasher(long a, long b, long prime) {
        this.a = a;
        this.b = b;
        this.prime = prime;
    }

    /**
     * Computes a randomized polynomial hash code for a given string.
     */
    public int hash(String str, int numBuckets) {
        if (str == null || str.isEmpty()) {
            return 0;
        }

        long hashValue = 0;
        for (int i = 0; i < str.length(); i++) {
            hashValue = (hashValue * 31 + str.charAt(i)) % prime;
        }

        // Apply universal randomized linear transformation: h(x) = (a * x + b) % prime
        long finalHash = (a * Math.abs(hashValue) + b) % prime;
        return (int) (Math.abs(finalHash) % numBuckets);
    }
}
