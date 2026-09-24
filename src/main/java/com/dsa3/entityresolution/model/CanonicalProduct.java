package com.dsa3.entityresolution.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a unified canonical product entry grouping matching seller listings.
 */
public class CanonicalProduct {
    private final String canonicalId;
    private String canonicalTitle;
    private String brand;
    private String category;
    private String representativeDescription;
    private final List<ProductListing> matchedListings;

    private double minPrice;
    private double maxPrice;
    private double averagePrice;

    public CanonicalProduct(String canonicalId, ProductListing initialListing) {
        this.canonicalId = canonicalId;
        this.matchedListings = new ArrayList<>();
        if (initialListing != null) {
            this.canonicalTitle = initialListing.getTitle();
            this.brand = initialListing.getBrand();
            this.category = initialListing.getCategory();
            this.representativeDescription = initialListing.getDescription();
            addListing(initialListing);
        } else {
            this.canonicalTitle = "Unknown Product";
            this.brand = "Generic";
            this.category = "General";
            this.representativeDescription = "";
            this.minPrice = 0.0;
            this.maxPrice = 0.0;
            this.averagePrice = 0.0;
        }
    }

    public synchronized void addListing(ProductListing listing) {
        if (!matchedListings.contains(listing)) {
            matchedListings.add(listing);
            recalculatePriceMetrics();
            // Update representative properties if initial listing had missing fields
            if ((brand == null || brand.isEmpty()) && listing.getBrand() != null) {
                brand = listing.getBrand();
            }
            if ((category == null || category.isEmpty()) && listing.getCategory() != null) {
                category = listing.getCategory();
            }
        }
    }

    public void recalculatePriceMetrics() {
        if (matchedListings.isEmpty()) {
            minPrice = 0.0;
            maxPrice = 0.0;
            averagePrice = 0.0;
            return;
        }
        double sum = 0.0;
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;

        for (ProductListing listing : matchedListings) {
            double p = listing.getPrice();
            sum += p;
            if (p < min) min = p;
            if (p > max) max = p;
        }
        this.minPrice = min;
        this.maxPrice = max;
        this.averagePrice = sum / matchedListings.size();
    }

    public String getCanonicalId() {
        return canonicalId;
    }

    public String getCanonicalTitle() {
        return canonicalTitle;
    }

    public void setCanonicalTitle(String canonicalTitle) {
        this.canonicalTitle = canonicalTitle;
    }

    public String getBrand() {
        return brand;
    }

    public String getCategory() {
        return category;
    }

    public String getRepresentativeDescription() {
        return representativeDescription;
    }

    public List<ProductListing> getMatchedListings() {
        return Collections.unmodifiableList(matchedListings);
    }

    public double getMinPrice() {
        return minPrice;
    }

    public double getMaxPrice() {
        return maxPrice;
    }

    public double getAveragePrice() {
        return averagePrice;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | Brand: %s | Sellers: %d | Price Range: $%.2f - $%.2f (Avg: $%.2f)",
                canonicalId, canonicalTitle, brand, matchedListings.size(), minPrice, maxPrice, averagePrice);
    }
}
