package com.dsa3.entityresolution.cli;

import com.dsa3.entityresolution.algorithms.graph.BipartiteGraph;
import com.dsa3.entityresolution.algorithms.graph.EdmondsKarpMaxFlow;
import com.dsa3.entityresolution.algorithms.matching.EntityResolutionEngine;
import com.dsa3.entityresolution.algorithms.matching.EntityResolutionEngine.ResolutionResult;
import com.dsa3.entityresolution.algorithms.string.EditDistance;
import com.dsa3.entityresolution.algorithms.string.KMPMatcher;
import com.dsa3.entityresolution.algorithms.string.RabinKarpMatcher;
import com.dsa3.entityresolution.algorithms.string.SuffixArray;
import com.dsa3.entityresolution.data.CsvProductLoader;
import com.dsa3.entityresolution.data.SampleDataGenerator;
import com.dsa3.entityresolution.model.CanonicalProduct;
import com.dsa3.entityresolution.model.ProductListing;
import com.dsa3.entityresolution.parallel.ParallelMatcher;
import com.dsa3.entityresolution.util.ConsoleTablePrinter;
import com.dsa3.entityresolution.util.Constants;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

/**
 * Main CLI menu for the E-Commerce Product Matching application.
 * Written in plain, simple language so anyone can understand and use it.
 */
public class ProductMatchingCLI {

    private final EntityResolutionEngine resolutionEngine;
    private final ParallelMatcher parallelMatcher;
    private List<ProductListing> currentListings;
    private ResolutionResult currentResolutionResult;

    public ProductMatchingCLI() {
        this.resolutionEngine = new EntityResolutionEngine();
        this.parallelMatcher = new ParallelMatcher();
    }

    public void start() {
        // Auto-generate sample CSV datasets if not present
        try {
            SampleDataGenerator.generateSampleDatasets(Constants.DEFAULT_SAMPLE_CSV, Constants.DEFAULT_LARGER_CSV);
        } catch (IOException e) {
            System.err.println("Note: Could not create sample data files: " + e.getMessage());
        }

        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            printMenu();
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.print("Enter the path to your CSV file: ");
                    String customPath = scanner.nextLine().trim();
                    loadDataset(customPath);
                    break;
                case "2":
                    loadDataset(Constants.DEFAULT_SAMPLE_CSV);
                    break;
                case "3":
                    findDuplicates();
                    break;
                case "4":
                    showMatchingProducts();
                    break;
                case "5":
                    showCleanCatalog();
                    break;
                case "6":
                    showPriceComparison();
                    break;
                case "7":
                    saveResultsToFile();
                    break;
                case "8":
                    showAdvancedOptions(scanner);
                    break;
                case "0":
                    exit = true;
                    System.out.println("\nGoodbye!");
                    break;
                default:
                    System.out.println("Please enter a number between 0 and 8.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n==================================================================");
        System.out.println("   E-Commerce Product Duplicate Finder & Price Comparison Tool");
        System.out.println("==================================================================");
        System.out.println("  What would you like to do?");
        System.out.println();
        System.out.println("  [1] Load products from my own CSV file");
        System.out.println("  [2] Load the built-in example product list");
        System.out.println("  [3] Find duplicate and matching products");
        System.out.println("  [4] Show which products are duplicates");
        System.out.println("  [5] Show clean list of unique products");
        System.out.println("  [6] Compare prices across different sellers");
        System.out.println("  [7] Save results to a file");
        System.out.println("  [8] Advanced tools");
        System.out.println("  [0] Exit");
        System.out.println("==================================================================");
    }

    private void loadDataset(String path) {
        try {
            currentListings = CsvProductLoader.loadFromCsv(path);
            currentResolutionResult = null;
            ConsoleTablePrinter.printListingsTable(currentListings);
            System.out.printf("\nDone! Loaded %d products from '%s'.%n", currentListings.size(), path);
        } catch (IOException e) {
            System.out.println("Could not open the file: " + e.getMessage());
            System.out.println("Tip: Make sure the file path is correct and the file is a valid CSV.");
        }
    }

    private void findDuplicates() {
        if (currentListings == null || currentListings.isEmpty()) {
            System.out.println("No products loaded yet. Loading the built-in example list first...");
            loadDataset(Constants.DEFAULT_SAMPLE_CSV);
        }

        System.out.println("\nScanning all products and looking for matches...");
        System.out.println("(This compares product names, descriptions, brands, and prices)");

        long startTime = System.currentTimeMillis();
        currentResolutionResult = resolutionEngine.resolveEntities(currentListings, true);
        long elapsed = System.currentTimeMillis() - startTime;

        int total = currentResolutionResult.getOriginalListings().size();
        int matches = currentResolutionResult.getConfirmedMatches().size();
        int groups = currentResolutionResult.getCanonicalCatalog().size();

        System.out.println("\nDone! Here is what was found:");
        System.out.printf("  - Products scanned       : %d%n", total);
        System.out.printf("  - Duplicate pairs found  : %d%n", matches);
        System.out.printf("  - Unique product groups  : %d%n", groups);
        System.out.printf("  - Time taken             : %d ms%n", elapsed);
    }

    private void showMatchingProducts() {
        if (currentResolutionResult == null) {
            System.out.println("You need to find duplicates first. Running that now...");
            findDuplicates();
        }
        ConsoleTablePrinter.printDuplicateMatches(currentResolutionResult.getConfirmedMatches());
    }

    private void showCleanCatalog() {
        if (currentResolutionResult == null) {
            System.out.println("You need to find duplicates first. Running that now...");
            findDuplicates();
        }
        ConsoleTablePrinter.printCanonicalCatalog(currentResolutionResult.getCanonicalCatalog());
    }

    private void showPriceComparison() {
        if (currentResolutionResult == null) {
            System.out.println("You need to find duplicates first. Running that now...");
            findDuplicates();
        }
        ConsoleTablePrinter.printPriceComparisonMatrix(currentResolutionResult.getCanonicalCatalog());
    }

    private void saveResultsToFile() {
        if (currentResolutionResult == null) {
            System.out.println("You need to find duplicates first. Running that now...");
            findDuplicates();
        }

        String outputPath = "data/results.csv";
        File file = new File(outputPath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("Product Group ID,Product Name,Brand,Category,Number of Sellers,Lowest Price,Highest Price,Average Price,Max Savings");
            for (CanonicalProduct cp : currentResolutionResult.getCanonicalCatalog()) {
                double savings = cp.getMaxPrice() - cp.getMinPrice();
                writer.printf("%s,\"%s\",%s,%s,%d,%.2f,%.2f,%.2f,%.2f%n",
                        cp.getCanonicalId(),
                        cp.getCanonicalTitle().replace("\"", "'"),
                        cp.getBrand(),
                        cp.getCategory(),
                        cp.getMatchedListings().size(),
                        cp.getMinPrice(),
                        cp.getMaxPrice(),
                        cp.getAveragePrice(),
                        savings);
            }
            System.out.println("\nResults saved to: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Could not save results: " + e.getMessage());
        }
    }

    private void showAdvancedOptions(Scanner scanner) {
        System.out.println("\n==================================================================");
        System.out.println("  Advanced Tools");
        System.out.println("==================================================================");
        System.out.println("  [1] Test how fast the program runs (Speed Test)");
        System.out.println("  [2] Test individual matching tools");
        System.out.println("  [0] Go back");
        System.out.println("==================================================================");
        System.out.print("Enter your choice: ");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                runSpeedTest();
                break;
            case "2":
                runMatchingToolTest(scanner);
                break;
            case "0":
                break;
            default:
                System.out.println("Invalid option.");
        }
    }

    private void runMatchingToolTest(Scanner scanner) {
        System.out.println("\n--- Test Individual Matching Tools ---");
        System.out.println("1. Find a word inside a sentence (KMP search)");
        System.out.println("2. Compare two product names and get a similarity score");
        System.out.println("3. Find common text in two descriptions");
        System.out.println("4. Flow-based assignment test (graph matching)");
        System.out.print("Pick a tool (1-4): ");
        String testChoice = scanner.nextLine().trim();

        switch (testChoice) {
            case "1":
                System.out.print("Enter a sentence: ");
                String text = scanner.nextLine();
                System.out.print("Enter a word or phrase to search for: ");
                String pattern = scanner.nextLine();
                List<Integer> kmpMatches = KMPMatcher.search(text.toLowerCase(), pattern.toLowerCase());
                if (kmpMatches.isEmpty()) {
                    System.out.println("Not found.");
                } else {
                    System.out.println("Found at position(s): " + kmpMatches);
                }
                break;
            case "2":
                System.out.print("Enter the first product name: ");
                String title1 = scanner.nextLine();
                System.out.print("Enter the second product name: ");
                String title2 = scanner.nextLine();
                double sim = EditDistance.computeSimilarity(title1, title2);
                System.out.printf("Similarity: %.1f%%%n", sim * 100);
                if (sim >= Constants.MATCH_THRESHOLD) {
                    System.out.println("These look like the SAME product.");
                } else if (sim >= Constants.POSSIBLE_MATCH_THRESHOLD) {
                    System.out.println("These might be the same product.");
                } else {
                    System.out.println("These look like DIFFERENT products.");
                }
                break;
            case "3":
                System.out.print("Enter the first description: ");
                String desc1 = scanner.nextLine();
                System.out.print("Enter the second description: ");
                String desc2 = scanner.nextLine();
                String lcs = SuffixArray.findLongestCommonSubstring(desc1, desc2);
                double saSim = SuffixArray.calculateDescriptionSimilarity(desc1, desc2);
                System.out.printf("Common text found: \"%s\"%n", lcs.isEmpty() ? "(none)" : lcs);
                System.out.printf("Overlap: %.1f%%%n", saSim * 100);
                break;
            case "4":
                BipartiteGraph graph = new BipartiteGraph(6);
                graph.addEdge(0, 1, 1.0);
                graph.addEdge(0, 2, 1.0);
                graph.addEdge(1, 3, 1.0);
                graph.addEdge(2, 3, 1.0);
                graph.addEdge(2, 4, 1.0);
                graph.addEdge(3, 5, 2.0);
                graph.addEdge(4, 5, 1.0);
                double maxFlow = EdmondsKarpMaxFlow.computeMaxFlow(graph, 0, 5);
                System.out.printf("Graph assignment result: %.0f products assigned successfully.%n", maxFlow);
                break;
            default:
                System.out.println("Invalid option.");
        }
    }

    private void runSpeedTest() {
        System.out.println("\nLoading 200+ product list for speed test...");
        try {
            List<ProductListing> largeListings = CsvProductLoader.loadFromCsv(Constants.DEFAULT_LARGER_CSV);
            System.out.printf("Loaded %d products.%n", largeListings.size());

            com.dsa3.entityresolution.algorithms.hashing.BlockingIndex index =
                    new com.dsa3.entityresolution.algorithms.hashing.BlockingIndex();
            index.indexListings(largeListings);
            List<com.dsa3.entityresolution.algorithms.hashing.BlockingIndex.ListingPair> pairs = index.getCandidatePairs();
            System.out.printf("Comparing %d product pairs...%n", pairs.size());

            long[] timings = parallelMatcher.benchmarkPerformance(pairs);
            ConsoleTablePrinter.printPerformanceBenchmark(timings[0], timings[1], pairs.size());

        } catch (IOException e) {
            System.out.println("Could not load the test dataset: " + e.getMessage());
        }
    }
}
