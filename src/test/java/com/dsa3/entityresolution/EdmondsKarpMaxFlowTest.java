package com.dsa3.entityresolution;

import com.dsa3.entityresolution.algorithms.graph.BipartiteGraph;
import com.dsa3.entityresolution.algorithms.graph.Edge;
import com.dsa3.entityresolution.algorithms.graph.EdmondsKarpMaxFlow;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EdmondsKarpMaxFlowTest {

    @Test
    public void testMaxFlowStandardBipartiteGraph() {
        // Graph: Source=0, Left={1, 2}, Right={3, 4}, Sink=5
        BipartiteGraph graph = new BipartiteGraph(6);

        // Source to Left
        graph.addEdge(0, 1, 1.0);
        graph.addEdge(0, 2, 1.0);

        // Left to Right candidates
        graph.addEdge(1, 3, 1.0);
        graph.addEdge(2, 3, 1.0);
        graph.addEdge(2, 4, 1.0);

        // Right to Sink
        graph.addEdge(3, 5, 1.0);
        graph.addEdge(4, 5, 1.0);

        double maxFlow = EdmondsKarpMaxFlow.computeMaxFlow(graph, 0, 5);
        assertEquals(2.0, maxFlow, 1e-6);

        List<Edge> matched = EdmondsKarpMaxFlow.getMatchedEdges(graph, 0, 5);
        assertEquals(2, matched.size());
    }
}
