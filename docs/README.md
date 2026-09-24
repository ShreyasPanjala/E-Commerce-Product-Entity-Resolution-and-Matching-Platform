# Product Intelligence & Catalog Entity Resolution Platform

An enterprise-grade Product Intelligence Console and Catalog Resolution Engine implemented in Java 17+. The platform resolves, matches, and merges identical or near-duplicate e-commerce product listings from multiple competing sellers into a unified, clean canonical product catalog.

---

## 📌 Business & Operational Problem

E-commerce marketplaces (such as Amazon, eBay, or Walmart) host millions of product listings submitted by independent sellers. Because sellers describe products differently—using varied titles, word reordering, abbreviations, typos, or differing specification formats—the same physical item appears under multiple distinct listings.

**Entity Resolution (ER)** is the computational process of identifying and linking records that refer to the same real-world entity. This system solves product catalog fragmentation by applying a multi-stage pipeline of core computer science algorithms: string matching, dynamic programming, suffix arrays, randomized hashing blocking, graph flow networks, and parallel multi-threaded processing.

---

## 💻 Technical Algorithmic Foundations

The application provides production-oriented catalog resolution leveraging classical algorithmic foundations:

| Algorithm | Domain | Engine Role | Complexity |
| :--- | :--- | :--- | :--- |
| **Knuth-Morris-Pratt (KMP)** | String Matching | Detects exact pattern & model substring matches in titles | $O(N + M)$ |
| **Rabin-Karp** | Rolling Hash | Identifies overlapping title fragments via polynomial hashing | $O(N + M)$ avg |
| **Suffix Array & LCP** | Advanced Strings | Analyzes description similarity & longest common substrings | $O(N \log N)$ |
| **Wagner-Fischer DP** | Dynamic Programming | Measures Levenshtein edit distance for typos and word variations | $O(M \times N)$ |
| **Bipartite Graph & Edmonds-Karp** | Graph & Max Flow | Models listings vs canonical products for optimal flow assignment | $O(V \cdot E^2)$ |
| **Greedy Approximation** | Greedy Clustering | Groups high-similarity candidate pairs into canonical clusters | $O(E \log E)$ |
| **Randomized Hashing / Blocking** | Hashing & Indexing | Eliminates $O(N^2)$ pair comparison cost via candidate buckets | $O(N)$ indexing |
| **Parallel Processing** | Multithreading | Distributes candidate pair similarity evaluation across CPU cores | Speedup $\approx P$ |

> [!NOTE]
> All algorithms are implemented **manually** in Java without relying on external algorithm libraries or enterprise frameworks.

---

## 🛠 Project Structure

```
.
├── pom.xml
├── src/main/java/com/dsa3/entityresolution/
│   ├── Main.java                        # Main application entry point
│   ├── cli/
│   │   └── ProductMatchingCLI.java      # Product Intelligence Console Dashboard
│   ├── model/
│   │   ├── ProductListing.java          # Seller product listing data model
│   │   ├── CanonicalProduct.java        # Grouped canonical catalog item model
│   │   ├── MatchResult.java             # Match metadata & explanation model
│   │   └── Seller.java                  # Seller profile model
│   ├── data/
│   │   ├── CsvProductLoader.java        # Pure Java CSV parser
│   │   └── SampleDataGenerator.java     # Dataset generator for sample CSVs
│   ├── preprocessing/
│   │   ├── TextNormalizer.java          # Lowercase, punctuation & stopword cleaner
│   │   └── Tokenizer.java               # Word, N-gram, and shingle tokenizer
│   ├── algorithms/
│   │   ├── string/
│   │   │   ├── KMPMatcher.java          # KMP algorithm & LPS array precomputation
│   │   │   ├── RabinKarpMatcher.java    # Rabin-Karp rolling hash string search
│   │   │   ├── SuffixArray.java         # Suffix Array & LCP array construction
│   │   │   └── EditDistance.java        # Wagner-Fischer Levenshtein DP
│   │   ├── graph/
│   │   │   ├── BipartiteGraph.java      # Directed flow network graph representation
│   │   │   ├── Edge.java                # Capacity, flow & residual edge model
│   │   │   └── EdmondsKarpMaxFlow.java  # BFS Ford-Fulkerson Max Flow implementation
│   │   ├── hashing/
│   │   │   ├── RandomizedHasher.java    # Universal polynomial hashing function
│   │   │   └── BlockingIndex.java       # Candidate blocking bucket indexer
│   │   └── matching/
│   │       ├── SimilarityScorer.java    # Weighted composite scoring engine
│   │       ├── GreedyMatcher.java       # Disjoint-set greedy clustering
│   │       └── EntityResolutionEngine.java # End-to-end pipeline orchestrator
│   ├── parallel/
│   │   └── ParallelMatcher.java         # Concurrent multi-threaded candidate matcher
│   └── util/
│       ├── ConsoleTablePrinter.java     # Formatted ASCII table renderer
│       └── Constants.java               # Similarity thresholds & algorithm weights
├── src/test/java/com/dsa3/entityresolution/
│   ├── KMPMatcherTest.java
│   ├── RabinKarpMatcherTest.java
│   ├── SuffixArrayTest.java
│   ├── EditDistanceTest.java
│   ├── EdmondsKarpMaxFlowTest.java
│   ├── GreedyMatcherTest.java
│   ├── EntityResolutionEngineTest.java
│   └── ParallelMatcherTest.java
├── data/
│   ├── sample_products.csv             # 25+ curated rows with realistic noise
│   └── larger_sample_products.csv      # 200+ generated rows for benchmarks
└── docs/
    ├── README.md                        # Overview and user manual
    ├── ALGORITHMS.md                    # In-depth algorithmic documentation
    └── SAMPLE_OUTPUT.md                 # CLI sample output transcript
```

---

## 🚀 How to Run the Application

### Prerequisites
- Java JDK 17 or later (Java 25 verified)
- Apache Maven 3.8+

### 1. Compile and Run Unit Tests
```bash
mvn clean test
```

### 2. Launch Product Intelligence Console
```bash
java -cp target/classes com.dsa3.entityresolution.Main
```
Or via Maven:
```bash
mvn exec:java -Dexec.mainClass="com.dsa3.entityresolution.Main"
```

---

## 🖥 Product Intelligence Console Dashboard

```text
==========================================================================================
  PRODUCT INTELLIGENCE CONSOLE
  Catalog Resolution Dashboard
==========================================================================================
 [1] Import product listings from CSV
 [2] Use bundled sample catalog
 [3] Run catalog entity resolution
 [4] Review matched product groups
 [5] View unified product catalog
 [6] Compare seller pricing
 [7] Export resolved catalog report
 [8] System diagnostics
 [0] Quit
==========================================================================================
```

### Console Feature Matrix
1. **Import product listings from CSV**: Supply custom CSV catalog file path.
2. **Use bundled sample catalog**: Loads default 25-row multi-seller catalog dataset.
3. **Run catalog entity resolution**: Executes normalization, blocking index, parallel scoring, and canonical catalog clustering.
4. **Review matched product groups**: Displays candidate matched listing pairs, similarity scores, and match explanations.
5. **View unified product catalog**: Displays unified canonical products with min/max/avg seller prices.
6. **Compare seller pricing**: Renders cross-seller price comparison matrix and potential buyer/seller savings.
7. **Export resolved catalog report**: Generates `data/resolved_catalog_report.csv` summary report.
8. **System diagnostics**: Submenu for multi-threaded performance benchmarking and internal engine diagnostics.
0. **Quit**: Exit application.

---

## 🌐 Shopcart Modern Web Frontend

A responsive web application inspired by the **Shopcart Electronics Store** design language (`#003D29` Deep Forest Green, `#F5C34B` Golden Yellow, `#F5F6F6` soft card containers, and Plus Jakarta Sans typography), fully updated with Indian Rupee (INR - ₹) currency and live multi-seller price intelligence.

### 🚀 Live Web Deployment
The frontend is deployed live on Netlify:
👉 **[https://ecommerce-entity-matching-shreyas.netlify.app](https://ecommerce-entity-matching-shreyas.netlify.app)**

### Accessing Locally
Alternatively, open `frontend/index.html` in any web browser, or launch from command line:
```bash
# On Windows PowerShell:
Start-Process "frontend\index.html"
```

### Core Frontend Views & Capabilities:
- **🛍️ Storefront Catalog**: Clean electronics cards showing canonical items, spec pills, green star ratings, verified seller counts, and instant seller price comparison.
- **🔍 Duplicate Resolution Groups**: Visual breakdown of canonical products mapped to raw marketplace listing variants, confidence badges, and lowest-price highlights.
- **📊 Multi-Seller Price Comparison Matrix**: Cross-seller price matrix table detailing best market prices, cheapest seller names, and maximum savings opportunities.
- **⚡ Live Matcher Sandbox**: Interactive playground for evaluating any two listing titles in real-time across Token Jaccard, Wagner-Fischer Edit Distance, and Substring Search.
- **🛒 Seller Offer Comparison Modal**: Drawer detailing all verified sellers offering a product with delivery times and best price badges.

