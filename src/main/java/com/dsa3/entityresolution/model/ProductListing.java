package com.dsa3.entityresolution.model;

import java.util.Collections;
import java.util.Map;

/**
 * Represents a single product listing published by a seller.
 */
public class ProductListing {
    private final String listingId;
    private final String sellerId;
    private final String sellerName;
    private final String title;
    private final String description;
    private final String brand;
    private final String category;
    private final double price;
    private final Map<String, String> specifications;

    private String normalizedTitle;
    private String normalizedDescription;

    public ProductListing(String listingId, String sellerId, String sellerName, String title,
                          String description, String brand, String category, double price,
                          Map<String, String> specifications) {
        this.listingId = listingId;
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.title = title;
        this.description = description;
        this.brand = brand;
        this.category = category;
        this.price = price;
        this.specifications = specifications != null ? specifications : Collections.emptyMap();
        this.normalizedTitle = "";
        this.normalizedDescription = "";
    }

    public String getListingId() {
        return listingId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getBrand() {
        return brand;
    }

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public Map<String, String> getSpecifications() {
        return specifications;
    }

    public String getNormalizedTitle() {
        return normalizedTitle;
    }

    public void setNormalizedTitle(String normalizedTitle) {
        this.normalizedTitle = normalizedTitle;
    }

    public String getNormalizedDescription() {
        return normalizedDescription;
    }

    public void setNormalizedDescription(String normalizedDescription) {
        this.normalizedDescription = normalizedDescription;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | Seller: %s | Brand: %s | $%.2f",
                listingId, title, sellerName, brand, price);
    }
}
