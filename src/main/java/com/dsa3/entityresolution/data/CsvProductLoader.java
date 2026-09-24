package com.dsa3.entityresolution.data;

import com.dsa3.entityresolution.model.ProductListing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Pure Java CSV Loader for parsing product listings from dataset files.
 */
public class CsvProductLoader {

    /**
     * Loads product listings from a CSV file path.
     */
    public static List<ProductListing> loadFromCsv(String filePath) throws IOException {
        List<ProductListing> listings = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            throw new IOException("CSV file not found at path: " + filePath);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                if (isHeader) {
                    isHeader = false; // Skip header line
                    continue;
                }

                List<String> tokens = parseCsvLine(line);
                if (tokens.size() >= 8) {
                    String listingId = tokens.get(0).trim();
                    String sellerId = tokens.get(1).trim();
                    String sellerName = tokens.get(2).trim();
                    String title = tokens.get(3).trim();
                    String description = tokens.get(4).trim();
                    String brand = tokens.get(5).trim();
                    String category = tokens.get(6).trim();
                    double price = parsePrice(tokens.get(7).trim());
                    Map<String, String> specs = tokens.size() > 8 ? parseSpecs(tokens.get(8).trim()) : new HashMap<>();

                    ProductListing listing = new ProductListing(
                            listingId, sellerId, sellerName, title, description, brand, category, price, specs);
                    listings.add(listing);
                }
            }
        }
        return listings;
    }

    /**
     * Robust CSV line parser handling comma delimiters and quoted fields.
     */
    private static List<String> parseCsvLine(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                tokens.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        tokens.add(sb.toString().trim());
        return tokens;
    }

    private static double parsePrice(String priceStr) {
        try {
            return Double.parseDouble(priceStr.replaceAll("[^0-9.]", ""));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private static Map<String, String> parseSpecs(String specsStr) {
        Map<String, String> specsMap = new HashMap<>();
        if (specsStr == null || specsStr.isEmpty()) {
            return specsMap;
        }
        String[] pairs = specsStr.split(";");
        for (String pair : pairs) {
            String[] kv = pair.split(":");
            if (kv.length == 2) {
                specsMap.put(kv[0].trim(), kv[1].trim());
            }
        }
        return specsMap;
    }
}
