package com.dsa3.entityresolution.algorithms.graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

/**
 * Edmonds-Karp Maximum Flow Algorithm Implementation (BFS-based Ford-Fulkerson).
 * 
 * Time Complexity: O(V * E^2) where V is the number of vertices and E is the number of edges.
 * Space Complexity: O(V + E) for BFS queues and parent edge tracking arrays.
 */
public class EdmondsKarpMaxFlow {

    /**
     * Computes the maximum flow from source to sink in the given flow network graph.
     */
    public static double computeMaxFlow(BipartiteGraph graph, int source, int sink) {
        int n = graph.getNumVertices();
        double maxFlow = 0.0;

        while (true) {
            // Edge tracking for path reconstruction
            Edge[] parentEdge = new Edge[n];
            Arrays.fill(parentEdge, null);

            Queue<Integer> queue = new ArrayDeque<>();
            queue.add(source);

            // BFS to find the shortest augmenting path in the residual network
            while (!queue.isEmpty() && parentEdge[sink] == null) {
                int curr = queue.poll();
                for (Edge edge : graph.getEdgesFrom(curr)) {
                    if (edge.getRemainingCapacity() > 1e-9 && parentEdge[edge.getV()] == null && edge.getV() != source) {
                        parentEdge[edge.getV()] = edge;
                        queue.add(edge.getV());
                    }
                }
            }

            // If sink could not be reached via augmenting path, maximum flow achieved
            if (parentEdge[sink] == null) {
                break;
            }

            // Find bottleneck capacity along the augmenting path
            double bottleneck = Double.MAX_VALUE;
            for (Edge edge = parentEdge[sink]; edge != null; edge = parentEdge[edge.getU()]) {
                bottleneck = Math.min(bottleneck, edge.getRemainingCapacity());
            }

            // Push bottleneck flow along the path and update residual capacities
            for (Edge edge = parentEdge[sink]; edge != null; edge = parentEdge[edge.getU()]) {
                edge.addFlow(bottleneck);
            }

            maxFlow += bottleneck;
        }

        return maxFlow;
    }

    /**
     * Extracts active matching edges (where flow > 0) after computing max flow.
     */
    public static List<Edge> getMatchedEdges(BipartiteGraph graph, int source, int sink) {
        List<Edge> matched = new ArrayList<>();
        for (int u = 0; u < graph.getNumVertices(); u++) {
            if (u == source || u == sink) continue;
            for (Edge edge : graph.getEdgesFrom(u)) {
                if (edge.getV() != source && edge.getV() != sink && edge.getFlow() > 1e-9) {
                    matched.add(edge);
                }
            }
        }
        return matched;
    }
}
