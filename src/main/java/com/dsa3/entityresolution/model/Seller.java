package com.dsa3.entityresolution.model;

/**
 * Represents an e-commerce seller listing products on the platform.
 */
public class Seller {
    private final String sellerId;
    private final String sellerName;
    private final double rating;
    private int totalListings;

    public Seller(String sellerId, String sellerName, double rating) {
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.rating = rating;
        this.totalListings = 0;
    }

    public String getSellerId() {
        return sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public double getRating() {
        return rating;
    }

    public int getTotalListings() {
        return totalListings;
    }

    public void incrementListings() {
        this.totalListings++;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) [Rating: %.1f★, Listings: %d]", sellerName, sellerId, rating, totalListings);
    }
}
