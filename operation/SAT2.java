package template.operation;

import template.graph_theory.BidirectionalGraph;
import template.graph_theory.CCTarjan;

/**
 * Created by dy on 16-12-20.
 */
public class SAT2 {

    BidirectionalGraph graph;
    int variables;

    public SAT2(int N) {
        if (N % 2 != 0) throw new RuntimeException();
        variables = N / 2;
        graph = new BidirectionalGraph(N);
    }

    public void addClosure(int a, boolean inva, int b, boolean invb) {
        if (!inva && !invb) {
            graph.addEdge(a + variables, b);
            graph.addEdge(b + variables, a);
        } else if (!inva && invb) {
            graph.addEdge(a + variables, b + variables);
            graph.addEdge(b, a);
        } else if (inva && !invb) {
            graph.addEdge(a, b);
            graph.addEdge(b + variables, a + variables);
        } else if (inva && invb) {
            graph.addEdge(a, b + variables);
            graph.addEdge(b, a + variables);
        }
    }

    public void removeClosure(int a, boolean inva, int b, boolean invb) {
        if (!inva && !invb) {
            graph.addEdge(a + variables, b);
            graph.addEdge(b + variables, a);
        } else if (!inva && invb) {
            graph.addEdge(a + variables, b + variables);
            graph.addEdge(b, a);
        } else if (inva && !invb) {
            graph.addEdge(a, b);
            graph.addEdge(b + variables, a + variables);
        } else if (inva && invb) {
            graph.addEdge(a, b + variables);
            graph.addEdge(b, a + variables);
        }
    }

    public boolean check() {
        int[] idscc = new CCTarjan(graph).whichSCC();
        for (int i = 0; i < variables; ++i)
            if (idscc[i] == idscc[i + variables]) return false;
        return true;
    }
}
