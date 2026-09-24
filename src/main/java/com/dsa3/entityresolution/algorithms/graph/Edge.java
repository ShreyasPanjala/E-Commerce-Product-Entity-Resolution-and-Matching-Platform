package com.dsa3.entityresolution.algorithms.graph;

/**
 * Directed edge representation for flow networks in Edmonds-Karp Max Flow algorithm.
 */
public class Edge {
    private final int u; // Source vertex ID
    private final int v; // Target vertex ID
    private final double capacity;
    private double flow;
    private Edge residualEdge;

    public Edge(int u, int v, double capacity) {
        this.u = u;
        this.v = v;
        this.capacity = capacity;
        this.flow = 0.0;
    }

    public int getU() {
        return u;
    }

    public int getV() {
        return v;
    }

    public double getCapacity() {
        return capacity;
    }

    public double getFlow() {
        return flow;
    }

    public void setFlow(double flow) {
        this.flow = flow;
    }

    public Edge getResidualEdge() {
        return residualEdge;
    }

    public void setResidualEdge(Edge residualEdge) {
        this.residualEdge = residualEdge;
    }

    public double getRemainingCapacity() {
        return capacity - flow;
    }

    public void addFlow(double delta) {
        this.flow += delta;
        this.residualEdge.flow -= delta;
    }

    @Override
    public String toString() {
        return String.format("%d -> %d (Flow: %.2f / Capacity: %.2f)", u, v, flow, capacity);
    }
}
