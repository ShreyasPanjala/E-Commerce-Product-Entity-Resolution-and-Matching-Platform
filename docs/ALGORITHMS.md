# Algorithmic Foundations & Mathematical Analysis

This document provides a detailed theoretical breakdown, time/space complexity analysis, and mathematical formulations for the core algorithms implemented in the **E-Commerce Product Entity Resolution and Matching Platform**.

---

## 1. Knuth-Morris-Pratt (KMP) String Matching Algorithm

### Overview & Purpose
The Knuth-Morris-Pratt (KMP) algorithm searches for occurrences of a pattern string $P$ of length $M$ within a text string $T$ of length $N$. In traditional naive string search, mismatched characters require backtracking the text pointer, resulting in $O(N \cdot M)$ worst-case time complexity. KMP avoids backtracking by precomputing a Longest Prefix Suffix (LPS) array $\pi$, allowing the text pointer to advance monotonically.

### Role in Entity Resolution
Used in `KMPMatcher.java` to detect exact model numbers (e.g., "WH-1000XM5", "M3 Max", "PG27UQR"), brand names, and key product attributes embedded inside varying seller title strings.

### Algorithm Details & Recurrence
The LPS array $\pi[i]$ stores the length of the longest proper prefix of $P[0 \dots i]$ that is also a suffix of $P[0 \dots i]$.

$$\pi[i] = \max \{ k : k < i \text{ and } P[0 \dots k-1] = P[i-k+1 \dots i] \}$$

- **Precomputation Time**: $O(M)$
- **Search Time**: $O(N)$
- **Total Time Complexity**: $O(N + M)$
- **Space Complexity**: $O(M)$ auxiliary memory for the $\pi$ table.

---

## 2. Rabin-Karp Rolling Hash Algorithm

### Overview & Purpose
The Rabin-Karp algorithm uses rolling polynomial hash functions to compute string signatures over a sliding window. Instead of recomputing hash values from scratch for every window shift, Rabin-Karp updates the hash in $O(1)$ constant time using modular arithmetic.

### Role in Entity Resolution
Used in `RabinKarpMatcher.java` to calculate fragment overlap scores across normalized product titles. It detects substring reorderings (e.g., "Apple iPhone 15" vs "iPhone 15 Apple") by sliding fixed-size $k$-gram windows across titles.

### Mathematical Formulation
Given a base $B = 256$ and prime modulus $Q = 10^9 + 7$, the hash $H$ of a window $S[i \dots i+M-1]$ is:

$$H(S[i \dots i+M-1]) = \left( \sum_{j=0}^{M-1} S[i+j] \cdot B^{M-1-j} \right) \pmod Q$$

When shifting the window from index $i$ to $i+1$, the rolling hash is updated in $O(1)$:

$$H_{i+1} = \left( B \cdot (H_i - S[i] \cdot B^{M-1}) + S[i+M] \right) \pmod Q$$

- **Average Time Complexity**: $O(N + M)$
- **Worst-Case Time Complexity**: $O(N \cdot M)$ (under extreme hash collisions)
- **Space Complexity**: $O(1)$ auxiliary space.

---

## 3. Suffix Array & Longest Common Prefix (LCP) Analysis

### Overview & Purpose
A Suffix Array is a lexicographically sorted array of all suffixes of a string $S$. Coupled with Kasai's algorithm for Longest Common Prefix (LCP) calculation, it enables efficient identification of repeating patterns, shared sub-phrases, and substring similarity across long texts.

### Role in Entity Resolution
Used in `SuffixArray.java` to compute Longest Common Substrings (LCS) between product descriptions. By concatenating two product descriptions with a unique delimiter $S_1 + \text{"\#"} + S_2$, the algorithm inspects adjacent suffixes in the combined Suffix Array spanning the delimiter to find matching description paragraphs.

### Complexity & Properties
- **Suffix Array Construction**: $O(N \log N)$ or $O(N \log^2 N)$ via custom suffix sorting.
- **LCP Array Construction (Kasai's Algo)**: $O(N)$ linear time.
- **LCS Query Time**: $O(N)$ sweep over the LCP array.
- **Space Complexity**: $O(N)$ for suffix integer array and LCP array.

---

## 4. Wagner-Fischer Dynamic Programming Edit Distance

### Overview & Purpose
The Wagner-Fischer algorithm computes the Levenshtein edit distance between two strings $S_1$ (length $M$) and $S_2$ (length $N$). Edit distance represents the minimum number of single-character insertions, deletions, or substitutions required to transform $S_1$ into $S_2$.

### Role in Entity Resolution
Used in `EditDistance.java` to gracefully handle seller typos, spelling mistakes ("Fifteen" vs "15", "Grey" vs "Gray"), and minor syntax differences. Raw edit distance is normalized into a similarity score $0.0 \dots 1.0$:

$$\text{Similarity}(S_1, S_2) = 1.0 - \frac{\text{LevenshteinDistance}(S_1, S_2)}{\max(|S_1|, |S_2|)}$$

### Dynamic Programming Recurrence
Let $DP[i][j]$ be the edit distance between $S_1[0 \dots i-1]$ and $S_2[0 \dots j-1]$:

$$DP[i][j] = \begin{cases} 
i & \text{if } j = 0 \\
j & \text{if } i = 0 \\
DP[i-1][j-1] & \text{if } S_1[i-1] = S_2[j-1] \\
1 + \min \begin{cases} DP[i-1][j] & \text{(Deletion)} \\ DP[i][j-1] & \text{(Insertion)} \\ DP[i-1][j-1] & \text{(Substitution)} \end{cases} & \text{otherwise}
\end{cases}$$

- **Time Complexity**: $O(M \times N)$
- **Space Complexity**: $O(M \times N)$ DP matrix.

---

## 5. Bipartite Graph Modeling & Edmonds-Karp Maximum Flow

### Overview & Purpose
The Edmonds-Karp algorithm computes the maximum network flow in a directed graph using Breadth-First Search (BFS) to discover shortest augmenting paths in the residual graph (a concrete realization of the Ford-Fulkerson method).

### Role in Entity Resolution
Used in `BipartiteGraph.java` and `EdmondsKarpMaxFlow.java` to model the assignment of individual seller product listings to candidate canonical product catalog entries.

### Network Topology
1. **Source ($v_0$)**: Connected to every listing node $L_i$ with capacity $C(v_0, L_i) = 1.0$.
2. **Listings ($L_i$)**: Connected to candidate Canonical Product nodes $CP_j$ with capacity $C(L_i, CP_j) = 1.0$ if composite similarity $\ge$ threshold.
3. **Canonical Products ($CP_j$)**: Connected to **Sink ($v_{\text{sink}}$)** with capacity $C(CP_j, v_{\text{sink}}) = K$ (maximum allowable listings per canonical cluster).

- **Time Complexity**: $O(V \cdot E^2)$ where $V$ is total vertices and $E$ is total directed edges.
- **Space Complexity**: $O(V + E)$ adjacency representation.

---

## 6. Greedy Approximation Matcher

### Overview & Purpose
When handling thousands of candidate product pairs, running graph flow optimizations on the full global network can become expensive. A Greedy Approximation algorithm sorts candidate pair matches by similarity score and partitions listings into canonical clusters using Disjoint Set Union (DSU / Union-Find) with path compression.

### Role in Entity Resolution
Implemented in `GreedyMatcher.java` for $O(E \log E)$ scalable resolution. It processes candidate listing pairs from highest to lowest similarity, merging matching listings into canonical products.

- **Time Complexity**: $O(E \log E + E \cdot \alpha(V))$ where $\alpha$ is the Inverse Ackermann function.
- **Space Complexity**: $O(V + E)$ for DSU parent mapping tables.

---

## 7. Randomized Hashing & Candidate Blocking Index

### Overview & Purpose
In naive pairwise entity resolution, comparing $N$ listings requires $\frac{N(N-1)}{2} = O(N^2)$ similarity evaluations. Blocking partitions records into smaller buckets based on randomized hash keys, ensuring that pairwise comparisons occur only between listings sharing candidate buckets.

### Role in Entity Resolution
Implemented in `RandomizedHasher.java` and `BlockingIndex.java`. Product listings are indexed into composite hash buckets combining:
1. `Brand:Category` keys
2. Universal polynomial hash buckets of token shingles: $h(x) = (a \cdot x + b) \pmod P \pmod B$

- **Indexing Time Complexity**: $O(N)$
- **Comparison Pair Reduction**: Reduces $O(N^2)$ down to $O(\sum |B_k|^2) \ll O(N^2)$.

---

## 8. Parallel Processing Engine

### Overview & Purpose
Candidate pair evaluations across independent hash buckets are embarrassingly parallel tasks. `ParallelMatcher.java` leverages Java's `ForkJoinPool` and parallel streams to evaluate candidate pairs concurrently across available CPU hardware threads.

- **Time Complexity**: $O\left( \frac{E \cdot T_{\text{eval}}}{P} \right)$ where $P$ is the number of CPU cores and $T_{\text{eval}}$ is pair scoring time.
- **Speedup**: Achieves near-linear speedup $S(P) \approx P$ on multi-core systems.
