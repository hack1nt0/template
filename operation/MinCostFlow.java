package template.operation;

import template.collection.IntArrayList;
import template.collection.tuple.Tuple2;
import template.graph_theory.AbstractEdge;
import template.graph_theory.BidirectionalGraph;

import java.util.*;

/**
 * Created by dy on 16-12-6.
 */
public class MinCostFlow {
    private class Edge extends AbstractEdge{
        private final int from;
        private final int to;
        private int cap;
        private final int cost;
        private final int capInit;
        private AbstractEdge rev;

        public Edge(int from, int to, int cap, int cost) {
            this.from = from;
            this.to = to;
            this.cap = this.capInit = cap;
            this.cost = cost;
        }

        @Override
        public void setReversalEdge(AbstractEdge reversalEdge) {
            this.rev = reversalEdge;
        }

        @Override
        public AbstractEdge getReversalEdge() {
            return rev;
        }

        @Override
        public int getCapacity() {
            return cap;
        }

        @Override
        public void setCapacity(int capacity) {
            this.cap = capacity;
        }

        @Override
        public int getCost() {
            return cost;
        }

        @Override
        public int getFrom() {
            return from;
        }

        @Override
        public int getTo() {
            return to;
        }

        @Override
        public int getFlow() {
            return capInit - cap;
        }

        @Override
        public String toString() {
            //return "(" + id + "," + rev.getId() + ")" + " " + from + "," + to + "," + cap + "," + cost;
            return getCapacity() + "," + getFlow() + "," + getCost();
        }
    }
    private BidirectionalGraph residualGraph;
    private int source, sink, flowNeeded, minCost, maxFlow;

    public MinCostFlow(BidirectionalGraph graph, int S, int T) {
        this(graph, S, T, -1);
    }

    public MinCostFlow(BidirectionalGraph graph, int S, int T, int flowNeeded) {
        this.source = S;
        this.sink = T;
        this.flowNeeded = flowNeeded;
        this.residualGraph = generateResidualGraph(graph);
        if (graph.hasNegativeCycle()) cycleCanceling();
        bellmanford();
    }

    private void bellmanford() {
        int leftFlow = flowNeeded;
        int[] d = new int[residualGraph.V()];
        int doo = Integer.MAX_VALUE;
        boolean[] inque = new boolean[residualGraph.V()];
        AbstractEdge[] edgeOnPath = new Edge[residualGraph.V()];


        while (true) {
            if (leftFlow == 0) break;

            Arrays.fill(d, doo);
            Arrays.fill(inque, false);
            d[source] = 0;
            IntArrayList que = new IntArrayList(residualGraph.V(), true);
            que.add(source);
            while (true) {
                if (que.isEmpty()) break;
                int from = que.poll();
                inque[from] = false;
                for (AbstractEdge e : residualGraph.adj(from)) {
                    if (e.getCapacity() <= 0) continue;
                    int to = e.other(from);

                    int a2b = d[from] + e.getCost();
                    //if (Math.abs(a2b - d[to]) <= 1e-6) continue;
                    if (a2b < d[to]) {
                        edgeOnPath[to] = e;
                        d[to] = a2b;
                        if (!inque[to]) {
                            que.add(to);
                            inque[to] = true;
                        }
                    }
                }
            }
            if (d[sink] == doo) {
                break;
            }
            int minflow = Integer.MAX_VALUE;
            for (int cur = sink; ; ) {
                AbstractEdge preE = edgeOnPath[cur];
                if (preE == null) break;
                minflow = Math.min(minflow, preE.getCapacity());
                cur = preE.other(cur);
            }
            for (int cur = sink; ; ) {
                AbstractEdge preE = edgeOnPath[cur];
                if (preE == null) break;
                preE.setCapacity(preE.getCapacity() - minflow);
                preE.getReversalEdge().setCapacity(preE.getReversalEdge().getCapacity() + minflow);
                cur = preE.other(cur);
            }
            if (flowNeeded > 0) {
                int deltaFlow = Math.min(minflow, leftFlow);
                minCost += deltaFlow * d[sink];
                leftFlow -= deltaFlow;
                maxFlow += deltaFlow;
            } else {
                minCost += minflow * d[sink];
                maxFlow += minflow;
            }
        }
    }

    private void cycleCanceling() {
        while (true) {
            boolean updated = false;
            int n = residualGraph.V();
            long[][] dist = new long[n][n];
            long oo = Long.MAX_VALUE;
            for (int i = 0; i < n; ++i) Arrays.fill(dist[i], oo);
            AbstractEdge[][] next = new AbstractEdge[n][n];
            for (int i = 0; i < n; ++i)
                for (AbstractEdge e : residualGraph.adj(i)) {
                    if (e.getCapacity() > 0) {
                        if (e.getCost() < 0) {
                            minCost += e.getCost() * e.getCapacity();
                            e.setCapacity(0);
                        } else {
                            dist[i][e.other(i)] = e.getCost();
                            next[i][e.other(i)] = e;
                        }
                    }
                }
            for (int k = 0; k < n; ++k)
                for (int i = 0; i < n; ++i)
                    for (int j = 0; j < n; ++j) {
                        if (i != k && j != k && dist[k][i] != oo && dist[j][k] != oo && dist[i][j] != oo && dist[i][j] + dist[j][k] + dist[k][i] < 0) {
                                int from = i;
                                int cost = 0;
                                int flow = Integer.MAX_VALUE;
                                while (true) {
                                    flow = Math.min(flow, next[from][j].getCapacity());
                                    if (flow == 0) break;
                                    cost += next[from][j].getCost();
                                    from = next[from][j].other(from);
                                    if (from == j) break;
                                }
                                if (flow == 0) continue;
                                updated = true;
                                if (cost != dist[i][j]) {
                                    throw new RuntimeException(cost + " " +  dist[i][j]);
                                }
                                minCost += cost * flow;
                                from = i;
                                while (true) {
                                    next[from][j].setCapacity(next[from][j].getCapacity() - flow);
                                    from = next[from][j].other(from);
                                    if (from == j) break;
                                }
                            }
                        if (dist[i][k] != oo && dist[k][j] != oo && dist[i][k] + dist[k][j] < dist[i][j]) {
                            dist[i][j] = dist[i][k] + dist[k][j];
                            next[i][j] = next[i][k];
                        }
                    }
            if (!updated) break;
        }
    }



    private BidirectionalGraph generateResidualGraph(BidirectionalGraph graph) {
        BidirectionalGraph residualGraph = new BidirectionalGraph(graph.V());
        int edgeCount = 0;
        for (int i = 0; i < graph.V(); ++i) {
            for (AbstractEdge e : graph.adj(i)) {
                int j = e.other(i);
                AbstractEdge eclone = new Edge(i, j, e.getCapacity(), e.getCost());
                AbstractEdge reversed = new Edge(j, i, 0, -e.getCost());
                eclone.setReversalEdge(reversed);
                reversed.setReversalEdge(eclone);
                residualGraph.addEdge(eclone);
                residualGraph.addEdge(reversed);
            }
        }
        return residualGraph;
    }

    public BidirectionalGraph getResidualGraph() {
        return residualGraph;
    }

    public BidirectionalGraph flowGraph() {
        BidirectionalGraph flowGraph = new BidirectionalGraph(residualGraph.V());
        for (int i = 0; i < residualGraph.V(); ++i) {
            for (AbstractEdge e : residualGraph.adj(i))
                if (e.getFlow() > 0) {
                    flowGraph.addEdge(e);
                }
        }
        return flowGraph;
    }

    public Tuple2<Integer, Integer> getFlowAndCost() {
        return new Tuple2<>(maxFlow, minCost);
    }
}
