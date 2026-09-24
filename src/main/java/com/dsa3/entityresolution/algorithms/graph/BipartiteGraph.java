package com.dsa3.entityresolution.algorithms.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Bipartite Graph / Flow Network representation for assigning product listings to canonical products.
 */
public class BipartiteGraph {

    private final int numVertices;
    private final List<List<Edge>> adj;
    private final Map<Integer, String> vertexLabels;

    public BipartiteGraph(int numVertices) {
        this.numVertices = numVertices;
        this.adj = new ArrayList<>(numVertices);
        for (int i = 0; i < numVertices; i++) {
            this.adj.add(new ArrayList<>());
        }
        this.vertexLabels = new HashMap<>();
    }

    public void addEdge(int u, int v, double capacity) {
        Edge forwardEdge = new Edge(u, v, capacity);
        Edge residualEdge = new Edge(v, u, 0.0);

        forwardEdge.setResidualEdge(residualEdge);
        residualEdge.setResidualEdge(forwardEdge);

        adj.get(u).add(forwardEdge);
        adj.get(v).add(residualEdge);
    }

    public void setVertexLabel(int vertex, String label) {
        vertexLabels.put(vertex, label);
    }

    public String getVertexLabel(int vertex) {
        return vertexLabels.getOrDefault(vertex, "Vertex " + vertex);
    }

    public int getNumVertices() {
        return numVertices;
    }

    public List<Edge> getEdgesFrom(int u) {
        return adj.get(u);
    }

    public List<List<Edge>> getAdjacencyList() {
        return adj;
    }
}
