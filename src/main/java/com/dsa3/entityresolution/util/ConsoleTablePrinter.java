package com.dsa3.entityresolution.util;

import com.dsa3.entityresolution.model.CanonicalProduct;
import com.dsa3.entityresolution.model.MatchResult;
import com.dsa3.entityresolution.model.ProductListing;

import java.util.List;

/**
 * Utility class for printing formatted tables and console output in plain, readable language.
 */
public class ConsoleTablePrinter {

    public static void printHeader(String title) {
        String border = "==================================================================";
        System.out.println("\n" + border);
        System.out.println("  " + title);
        System.out.println(border);
    }

    public static void printSubHeader(String subtitle) {
        System.out.println("\n--- " + subtitle + " ---");
    }

    public static void printListingsTable(List<ProductListing> listings) {
        printHeader("All Loaded Products (" + listings.size() + " total)");
        System.out.printf("%-10s | %-12s | %-32s | %-12s | %-10s%n",
                "ID", "Seller", "Product Name", "Brand", "Price");
        System.out.println("------------------------------------------------------------------");
        for (ProductListing p : listings) {
            String shortTitle = p.getTitle().length() > 30 ? p.getTitle().substring(0, 27) + "..." : p.getTitle();
            String shortSeller = p.getSellerName().length() > 10 ? p.getSellerName().substring(0, 8) + ".." : p.getSellerName();
            System.out.printf("%-10s | %-12s | %-32s | %-12s | $%-9.2f%n",
                    p.getListingId(), shortSeller, shortTitle, p.getBrand(), p.getPrice());
        }
        System.out.println("------------------------------------------------------------------");
    }

    public static void printCanonicalCatalog(List<CanonicalProduct> catalog) {
        printHeader("Clean Product List — " + catalog.size() + " Unique Products Found");
        for (CanonicalProduct cp : catalog) {
            System.out.printf("%n  Product ID  : %s%n", cp.getCanonicalId());
            System.out.printf("  Name        : %s%n", cp.getCanonicalTitle());
            System.out.printf("  Brand       : %s  |  Category: %s%n", cp.getBrand(), cp.getCategory());
            System.out.printf("  Price Range : $%.2f (lowest) — $%.2f (highest)  |  Average: $%.2f%n",
                    cp.getMinPrice(), cp.getMaxPrice(), cp.getAveragePrice());
            System.out.printf("  Sellers     : %d seller(s) offer this product%n", cp.getMatchedListings().size());
            for (ProductListing listing : cp.getMatchedListings()) {
                System.out.printf("               • %-15s  $%.2f  → \"%s\"%n",
                        listing.getSellerName(), listing.getPrice(), listing.getTitle());
            }
            System.out.println("  ------------------------------------------------------------------");
        }
    }

    public static void printPriceComparisonMatrix(List<CanonicalProduct> catalog) {
        printHeader("Price Comparison — Best Deals Across Sellers");
        System.out.printf("%-20s | %-28s | %-10s | %-10s | %-10s | %-10s%n",
                "Product ID", "Product Name", "Lowest $", "Highest $", "Average $", "You Save");
        System.out.println("------------------------------------------------------------------");
        for (CanonicalProduct cp : catalog) {
            String shortTitle = cp.getCanonicalTitle().length() > 26 ?
                    cp.getCanonicalTitle().substring(0, 23) + "..." : cp.getCanonicalTitle();
            double savings = cp.getMaxPrice() - cp.getMinPrice();
            System.out.printf("%-20s | %-28s | $%-9.2f | $%-9.2f | $%-9.2f | $%-9.2f%n",
                    cp.getCanonicalId(), shortTitle,
                    cp.getMinPrice(), cp.getMaxPrice(), cp.getAveragePrice(), savings);
        }
        System.out.println("------------------------------------------------------------------");
        System.out.println("  Tip: 'You Save' = difference between cheapest and most expensive seller.");
    }

    public static void printDuplicateMatches(List<MatchResult> matches) {
        if (matches.isEmpty()) {
            printHeader("No Duplicate Products Found");
            System.out.println("  All products appear to be unique.");
            return;
        }
        printHeader("Duplicate Products Found — " + matches.size() + " matching pairs");
        System.out.printf("%-10s <-> %-10s | %-8s | %s%n",
                "Product A", "Product B", "Match %", "Verdict");
        System.out.println("------------------------------------------------------------------");
        for (MatchResult m : matches) {
            int pct = (int) (m.getSimilarityScore() * 100);
            String verdict = m.getSimilarityScore() >= 0.75 ? "Very likely same product"
                    : "Possibly the same product";
            System.out.printf("%-10s <-> %-10s | %6d%%   | %s%n",
                    m.getListingA().getListingId(),
                    m.getListingB() != null ? m.getListingB().getListingId() : "N/A",
                    pct,
                    verdict);
        }
        System.out.println("------------------------------------------------------------------");
    }

    public static void printPerformanceBenchmark(long seqTimeMs, long parTimeMs, int pairsCount) {
        printHeader("Speed Test Results");
        System.out.printf("  Product pairs compared    : %d%n", pairsCount);
        System.out.printf("  Normal (single-core) speed: %d ms%n", seqTimeMs);
        System.out.printf("  Fast (multi-core) speed   : %d ms%n", parTimeMs);
        double speedup = parTimeMs > 0 ? (double) seqTimeMs / parTimeMs : 1.0;
        System.out.printf("  Speed improvement         : %.2fx faster with multiple cores%n", speedup);
        System.out.println("==================================================================");
    }
}
