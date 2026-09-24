package com.dsa3.entityresolution.data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

/**
 * Utility class to generate sample CSV product datasets containing realistic duplicates,
 * word permutations, typos, and price variations across sellers.
 */
public class SampleDataGenerator {

    /**
     * Generates both sample_products.csv (25+ curated rows) and larger_sample_products.csv (200+ rows).
     */
    public static void generateSampleDatasets(String samplePath, String largerSamplePath) throws IOException {
        createDirectoryIfMissing(samplePath);
        createDirectoryIfMissing(largerSamplePath);

        generateCuratedSampleCsv(samplePath);
        generateLargerSampleCsv(largerSamplePath, 220);
    }

    private static void createDirectoryIfMissing(String filePath) {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
    }

    public static void generateCuratedSampleCsv(String filePath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("listingId,sellerId,sellerName,title,description,brand,category,price,specifications");

            // Apple iPhone 15 Cluster
            writer.println("L001,S01,TechHub,\"Apple iPhone 15 128GB Black\",\"Latest Apple iPhone 15 with 128GB storage in Midnight Black edition.\",Apple,Smartphones,799.00,\"storage:128GB;color:Black;ram:6GB\"");
            writer.println("L002,S02,iStore,\"iPhone 15 Apple 128 GB - Black\",\"Brand new Apple iPhone 15 smartphone 128GB Midnight Black.\",Apple,Smartphones,789.99,\"storage:128GB;color:Black\"");
            writer.println("L003,S03,GadgetZone,\"Apple iPhone Fifteen 128GB Black\",\"Original Apple iPhone Fifteen phone 128GB Black edition.\",Apple,Smartphones,805.50,\"storage:128GB;color:Black;screen:6.1\"");
            writer.println("L004,S04,MegaRetail,\"Apple iPhone 15 (128GB) - Midnight Black\",\"Apple iPhone 15 smartphone with Super Retina XDR display.\",Apple,Smartphones,795.00,\"storage:128GB;color:Black\"");

            // Samsung Galaxy S24 Cluster
            writer.println("L005,S01,TechHub,\"Samsung Galaxy S24 256GB\",\"Flagship Samsung Galaxy S24 smartphone with 256GB storage and AI camera.\",Samsung,Smartphones,850.00,\"storage:256GB;color:Onyx Black\"");
            writer.println("L006,S05,GalaxyStore,\"Galaxy S24 Samsung 256 GB Smartphone\",\"Samsung Galaxy S24 5G AI Phone 256GB Storage.\",Samsung,Smartphones,840.00,\"storage:256GB;display:6.2\"");
            writer.println("L007,S02,iStore,\"Samsung Galaxy S24 (256GB, Onyx Black)\",\"Samsung S24 smartphone 256GB edition.\",Samsung,Smartphones,859.99,\"storage:256GB;color:Onyx Black\"");

            // Sony WH-1000XM5 Cluster
            writer.println("L008,S06,AudioWorld,\"Sony WH-1000XM5 Wireless Headphones\",\"Industry leading noise cancelling wireless over-ear headphones by Sony.\",Sony,Audio,398.00,\"type:Over-Ear;noise_cancelling:Yes\"");
            writer.println("L009,S03,GadgetZone,\"Sony Noise Cancelling Headphones WH1000XM5\",\"Premium Sony WH1000XM5 wireless noise cancelling headset.\",Sony,Audio,389.50,\"type:Over-Ear;color:Black\"");
            writer.println("L010,S07,SoundExpress,\"Sony WH1000XM5/B Wireless Over-Ear Headphone\",\"Sony WH 1000XM5 Bluetooth ANC active noise control headphones.\",Sony,Audio,399.99,\"type:Over-Ear;connectivity:Bluetooth\"");

            // MacBook Pro 16 Cluster
            writer.println("L011,S02,iStore,\"Apple MacBook Pro 16-inch M3 Max 36GB 1TB\",\"Apple MacBook Pro 16 M3 Max chip with 36GB Unified Memory 1TB SSD Space Black.\",Apple,Laptops,3499.00,\"ram:36GB;storage:1TB;chip:M3 Max\"");
            writer.println("L012,S04,MegaRetail,\"MacBook Pro 16 M3 Max 36GB RAM 1TB SSD\",\"Apple 16 MacBook Pro Laptop with M3 Max 36GB Unified RAM 1TB SSD.\",Apple,Laptops,3449.99,\"ram:36GB;storage:1TB\"");

            // Dell XPS 15 Cluster
            writer.println("L013,S01,TechHub,\"Dell XPS 15 9530 Laptop Intel i9 32GB 1TB OLED\",\"Dell XPS 15 laptop with 13th Gen Intel Core i9 32GB RAM 1TB SSD OLED touch.\",Dell,Laptops,2199.00,\"ram:32GB;storage:1TB;display:OLED\"");
            writer.println("L014,S08,PCWorld,\"Dell XPS 15 9530 i9-13900H 32GB DDR5 1TB SSD\",\"Premium Dell XPS 15 inch laptop Intel Core i9 32GB memory 1TB solid state drive.\",Dell,Laptops,2150.00,\"ram:32GB;storage:1TB\"");

            // Bose QuietComfort Ultra (No match for Sony)
            writer.println("L015,S06,AudioWorld,\"Bose QuietComfort Ultra Wireless Headphones\",\"Bose QuietComfort Ultra bluetooth active noise cancelling headphones.\",Bose,Audio,429.00,\"type:Over-Ear;noise_cancelling:Yes\"");

            // Logitech MX Master 3S Mouse (Distinct)
            writer.println("L016,S01,TechHub,\"Logitech MX Master 3S Wireless Performance Mouse\",\"Logitech MX Master 3S ergonomic wireless mouse with 8K DPI sensor.\",Logitech,Accessories,99.99,\"connectivity:Bluetooth/USB;dpi:8000\"");
            writer.println("L017,S05,GalaxyStore,\"Logitech MX Master 3S Ergonomic Bluetooth Mouse\",\"Logitech MX Master 3S wireless mouse for Mac and PC.\",Logitech,Accessories,94.50,\"connectivity:Bluetooth\"");

            // Apple iPad Air 5th Gen
            writer.println("L018,S02,iStore,\"Apple iPad Air 5th Gen 64GB Wi-Fi Space Gray\",\"Apple iPad Air 10.9-inch display 64GB M1 chip Space Gray.\",Apple,Tablets,599.00,\"storage:64GB;chip:M1\"");
            writer.println("L019,S04,MegaRetail,\"iPad Air 5 Apple 64 GB Space Grey M1\",\"Apple iPad Air 5th Generation 64GB Space Grey tablet.\",Apple,Tablets,579.99,\"storage:64GB;color:Space Gray\"");

            // ASUS ROG Swift Monitor
            writer.println("L020,S08,PCWorld,\"ASUS ROG Swift 27-inch 4K 144Hz Gaming Monitor\",\"ASUS ROG Swift PG27UQR 27\" 4K UHD 144Hz IPS gaming monitor.\",ASUS,Monitors,699.00,\"screen:27;refresh:144Hz\"");
            writer.println("L021,S01,TechHub,\"ASUS ROG Swift PG27UQR 27 4K UHD 144Hz Monitor\",\"ASUS ROG Swift 27 inch 4K UHD gaming display 144Hz response.\",ASUS,Monitors,689.50,\"screen:27;resolution:4K\"");

            // Google Pixel 8 Pro
            writer.println("L022,S03,GadgetZone,\"Google Pixel 8 Pro 128GB Bay Blue\",\"Google Pixel 8 Pro unlocked Android smartphone with Tensor G3 chip.\",Google,Smartphones,999.00,\"storage:128GB;color:Bay Blue\"");
            writer.println("L023,S07,SoundExpress,\"Pixel 8 Pro Google 128 GB Smartphone - Bay Blue\",\"Google Pixel 8 Pro 5G phone 128GB Bay Blue edition.\",Google,Smartphones,979.00,\"storage:128GB;color:Bay Blue\"");

            // Sony PlayStation 5 Slim
            writer.println("L024,S04,MegaRetail,\"Sony PlayStation 5 PS5 Slim Console Digital Edition\",\"Sony PS5 Slim digital console with 1TB SSD.\",Sony,Gaming,449.99,\"storage:1TB;edition:Digital\"");
            writer.println("L025,S05,GalaxyStore,\"PS5 Digital Edition Sony PlayStation 5 Slim 1TB\",\"Sony PlayStation 5 Slim digital video game console 1TB.\",Sony,Gaming,449.00,\"storage:1TB\"");
        }
    }

    public static void generateLargerSampleCsv(String filePath, int totalRows) throws IOException {
        String[] brands = {"Apple", "Samsung", "Sony", "Dell", "Bose", "Logitech", "ASUS", "Lenovo", "Google", "HP"};
        String[] categories = {"Smartphones", "Laptops", "Audio", "Monitors", "Tablets", "Accessories"};
        String[] sellers = {"TechHub", "iStore", "GadgetZone", "MegaRetail", "GalaxyStore", "AudioWorld", "SoundExpress", "PCWorld"};

        String[][] baseProducts = {
                {"iPhone 15 Pro Max 256GB Natural Titanium", "Apple flagship smartphone with A17 Pro chip.", "Apple", "Smartphones", "1199.00"},
                {"Galaxy S24 Ultra 512GB Titanium Black", "Samsung AI smartphone with S Pen.", "Samsung", "Smartphones", "1299.00"},
                {"MacBook Air 15 M2 8GB 256GB Midnight", "Thin and light Apple M2 laptop 15-inch display.", "Apple", "Laptops", "1299.00"},
                {"Dell XPS 13 9315 Intel i7 16GB 512GB", "Ultra-portable Dell XPS 13 laptop.", "Dell", "Laptops", "1099.00"},
                {"Sony WH-CH720N Noise Cancelling Headphones", "Affordable Sony wireless over-ear noise cancelling headset.", "Sony", "Audio", "148.00"},
                {"Bose SoundLink Flex Bluetooth Speaker", "Portable waterproof wireless speaker from Bose.", "Bose", "Audio", "149.00"},
                {"Logitech MX Keys S Wireless Keyboard", "Advanced wireless illuminated keyboard from Logitech.", "Logitech", "Accessories", "109.99"},
                {"ASUS TUF Gaming 27-inch 165Hz Monitor", "ASUS TUF Gaming 1080p 165Hz IPS monitor.", "ASUS", "Monitors", "199.00"},
                {"Lenovo ThinkPad X1 Carbon Gen 11 i7 32GB 1TB", "Lenovo business flagship laptop ThinkPad X1.", "Lenovo", "Laptops", "1849.00"},
                {"Google Pixel Watch 2 Matte Black", "Google smart watch with Fitbit health tracking.", "Google", "Accessories", "349.00"}
        };

        Random rand = new Random(12345); // Fixed seed for reproducible generation

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("listingId,sellerId,sellerName,title,description,brand,category,price,specifications");

            for (int i = 1; i <= totalRows; i++) {
                String listingId = String.format("L%04d", i);
                int baseIdx = (i - 1) % baseProducts.length;
                String[] base = baseProducts[baseIdx];

                String seller = sellers[rand.nextInt(sellers.length)];
                String sellerId = "S" + String.format("%02d", rand.nextInt(20) + 1);

                // Add variation/noise to titles and prices
                String title = generateTitleVariation(base[0], i, rand);
                double basePrice = Double.parseDouble(base[4]);
                double priceVariation = basePrice * (0.92 + (rand.nextDouble() * 0.16)); // +/- 8% price noise

                writer.printf("%s,%s,%s,\"%s\",\"%s\",%s,%s,%.2f,\"storage:256GB;gen:%d\"%n",
                        listingId, sellerId, seller, title, base[1], base[2], base[3], priceVariation, (i % 5) + 1);
            }
        }
    }

    private static String generateTitleVariation(String baseTitle, int index, Random rand) {
        int variationType = index % 4;
        switch (variationType) {
            case 0:
                return baseTitle; // Exact title
            case 1:
                return baseTitle + " (New Edition " + (2024 + (index % 2)) + ")";
            case 2:
                // Word swap
                String[] words = baseTitle.split(" ");
                if (words.length >= 3) {
                    return words[1] + " " + words[0] + " " + String.join(" ", java.util.Arrays.copyOfRange(words, 2, words.length));
                }
                return baseTitle + " Special";
            case 3:
                return baseTitle.replace("GB", " GB").replace("M2", "Apple M2");
            default:
                return baseTitle;
        }
    }
}
