package template.operation;

import template.collection.sequence.ArrayUtils;
import template.collection.IntArrayList;
import template.graph_theory.AbstractEdge;
import template.graph_theory.BidirectionalGraph;

import java.util.*;

/**
 * Created by dy on 16-10-1.
 */
public class MaxFlow {

    private List<AbstractEdge> edges; // only real edges, without RESIDUAL edges.
    private int[] dist;
    private int[] curE;
    private BidirectionalGraph residualGraph;
    private int source, sink, maxFlow;
    private List<AbstractEdge> minCuts;

    public MaxFlow(BidirectionalGraph graph, int source, int sink, List<AbstractEdge> minCuts) {
        edges = new ArrayList<>();
        dist = new int[graph.V()];
        curE = new int[graph.V()];
        this.residualGraph = generateResidualGraph(graph);
        this.source = source;
        this.sink = sink;
        this.maxFlow = dinic(minCuts);
        this.minCuts = minCuts;
    }

    public MaxFlow(BidirectionalGraph graph, int source, int sink) {
        edges = new ArrayList<>();
        dist = new int[graph.V()];
        curE = new int[graph.V()];
        this.residualGraph = generateResidualGraph(graph);
        this.source = source;
        this.sink = sink;
        this.maxFlow = dinic();
    }

    private BidirectionalGraph generateResidualGraph(BidirectionalGraph graph) {
        BidirectionalGraph residualGraph = new BidirectionalGraph(graph.V());
        int edgeCount = 0;
        for (int i = 0; i < graph.V(); ++i) {
            for (AbstractEdge e : graph.adj(i)) {
                int j = e.other(i);
                AbstractEdge eclone = new Edge(i, j, e.getCapacity(), e.getId());
                edges.add(eclone);
                AbstractEdge reversed = new Edge(j, i, 0, -1);
                eclone.setReversalEdge(reversed);
                reversed.setReversalEdge(eclone);
                residualGraph.addEdge(eclone);
                residualGraph.addEdge(reversed);
            }
        }
        return residualGraph;
    }

    public static AbstractEdge createEdge(int from, int to, int capacity, int id) {
        return new Edge(from, to, capacity, id);
    }

    private static class Edge extends AbstractEdge {
        private int initCap;
        private int cap;
        private AbstractEdge rev;
        private int from, to, id;
        //private int id = id;

        public Edge(int from, int to, int capacity, int id) {
            this.from = from;
            this.to = to;
            this.initCap = this.cap = capacity;
            this.id = id;
        }
        @Override
        public int getId() {
            return id;
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
        public int getInitialCapacity() {
            return initCap;
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
        public int getFrom() {
            return from;
        }

        @Override
        public int getTo() {
            return to;
        }

        @Override
        public String toString() {
            //return "(" + id + "," + rev.getId() + ")" + " " + from + "," + b + "," + cap + "," + cost;
            return getId() + "," + getCapacity() + "," + getFlow();
        }
    }

    private int dinic() {
        return dinic(null);
    }

    private int dinic(List<AbstractEdge> cuts) {
        reset();
        int maxflow = 0;
        while (true) {
            int dt = bfs(source, sink);
            if (dt == Integer.MAX_VALUE) break;
            Arrays.fill(curE, 0);
            while (true) {
                int f = dfs(source, sink, Integer.MAX_VALUE);
                if (f <= 0) break;
                maxflow += f;
            }
        }
        if (cuts != null) {
            /**
             * Found min-min-cut-set (min-num-edges & alphabet order of edge ids)
             */
            Integer[] index = (Integer[])ArrayUtils.inbox(ArrayUtils.index(edges.size()));
            Arrays.sort(index, new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {
                    AbstractEdge e1 = edges.get(o1), e2 = edges.get(o2);
                    int c1 = e1.getCapacity() + e1.getFlow();
                    int c2 = e2.getCapacity() + e2.getFlow();
                    if (c1 != c2) return c2 - c1;
                    return e1.getId() - e2.getId();
                }
            });
            int flows = maxflow;
            for (int rank = 0; rank < edges.size(); ++rank) {
                if (flows == 0) break;
                AbstractEdge e = edges.get(index[rank]);
                e.setVisited();
                int curFlow = dinic();
                if (flows - curFlow == e.getInitialCapacity()) {
                    flows = curFlow;
                    cuts.add(e);
                    continue;
                }
                e.notVisited();
            }

            /**
             * Found an arbitrary min-cut-set
             */
//            boolean[] S = new boolean[N];
//            cutsHelper(s, S);
//            for (AbstractEdge e : edges) if (S[e.getFlow()] && !S[e.getTo()] && e.getFlow() > 0) cuts.add(e);

        }
        return maxflow;
    }

    /**
     * MAKE SURE the class(same graph structure) can be invoked (dinic Linkage) multiplied times.
     * Used in finding min cuts.
     */
    private void reset() {
        for (AbstractEdge e : edges) {
            e.setCapacity(e.getInitialCapacity());
            AbstractEdge rev = e.getReversalEdge();
            rev.setCapacity(0);
        }
    }

    private int dfs(int s, int t, int curMinc) {
        if (s == t) return curMinc;

        while (curE[s] < residualGraph.adj(s).size()) {
            AbstractEdge e = residualGraph.adj(s).get(curE[s]++);
            if (e.getVisited()) continue; // dynamic deletion of edge
            int chd = e.other(s);
            if (e.getCapacity() > 0 && dist[chd] > dist[s]) {
                int minc = dfs(chd, t, Math.min(curMinc, e.getCapacity()));
                if (minc > 0) {
                    e.setCapacity(e.getCapacity() - minc);
                    AbstractEdge rev = e.getReversalEdge();
                    rev.setCapacity(rev.getCapacity() + minc);
                    return minc;
                }
            }
        }

        return 0;
    }

    private int bfs(int s, int t) {
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[s] = 0;
        //Queue<Integer> que = new LinkedList<Integer>();
        IntArrayList que = new IntArrayList(residualGraph.V(), true);
        que.add(s);
        while (!que.isEmpty()) {
            int cur = que.poll();
            if (cur == t) break;
            for (AbstractEdge e : residualGraph.adj(cur)) {
                if (e.getVisited()) continue; // dynamic deletion of edge
                int chd = e.other(cur);
                if (dist[chd] != Integer.MAX_VALUE || e.getCapacity() <= 0) continue;
                dist[chd] = dist[cur] + 1;
                que.add(chd);
            }
        }
        return dist[t];
    }

    public int getMaxFlow() {
        return maxFlow;
    }

    public List<AbstractEdge> getMinCuts() {
        return minCuts;
    }

    public void show() {
        BidirectionalGraph graph = new BidirectionalGraph(residualGraph.V());
        for (AbstractEdge e : edges) graph.addEdge(e);
        graph.show();
    }
}
