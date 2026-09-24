package com.dsa3.entityresolution;

import com.dsa3.entityresolution.cli.ProductMatchingCLI;

/**
 * Main application launcher for E-Commerce Product Entity Resolution and Matching Platform.
 */
public class Main {
    public static void main(String[] args) {
        ProductMatchingCLI cli = new ProductMatchingCLI();
        cli.start();
    }
}
