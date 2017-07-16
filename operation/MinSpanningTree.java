package template.operation;

import template.graph_theory.AbstractEdge;
import template.graph_theory.BidirectionalGraph;

import java.util.*;

/**
 * Created by dy on 16-12-3.
 */
public class MinSpanningTree {
    public class Edge extends AbstractEdge implements Comparable<Edge> {
        public int from, to;
        public int cost;

        public Edge(int a, int to, int cost) {
            this.from = a;
            this.to = to;
            this.cost = cost;
        }

        @Override
        public int compareTo(Edge o) {
            if (cost > o.cost) return 1;
            if (cost < o.cost) return -1;
            return 0;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Edge edge = (Edge) o;

            if (from != edge.from) return false;
            return to == edge.to;

        }

        @Override
        public int hashCode() {
            int result = from;
            result = 31 * result + to;
            return result;
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
    }

    int N, M;
    public List<Edge>[] adj;
    public Map<Edge, Integer> edges;
    public boolean needUpd = false;

    public MinSpanningTree(int N) {
        this.N = N;
        adj = new List[N];
        for (int i = 0; i < N; ++i) adj[i] = new ArrayList<Edge>();
        edges = new HashMap<Edge, Integer>();
    }

    public void addE (int a, int b, int cost) {
        if (a == b) return;
        needUpd = true;
        Edge edge = new Edge(a, b, cost);
        if (edges.containsKey(edge) && edges.get(edge) <= cost) return;
        edges.put(edge, cost);
        M++;
    }

    public void recreateAdj() {
        for (List<Edge> elist : adj) elist.clear();
        for (Edge e : edges.keySet()) {
            e.cost = edges.get(e);
            adj[e.from].add(e);
        }
        needUpd = false;
    }

    public long kruskal() {
        if (needUpd) {
            needUpd = false;
            recreateAdj();
        }

        boolean[] vis = new boolean[N];
        vis[0] = true;
        int nvis = 1;
        long ret = 0;
        PriorityQueue<Edge> que = new PriorityQueue<Edge>();
        for (Edge e : adj[0]) que.add(e);
        while (true) {
            if (nvis == N) break;
            if (que.isEmpty()) break;
            Edge cure = que.poll();
            if (vis[cure.to]) continue;
            vis[cure.to] = true;
            ret += cure.cost;
            nvis++;
            for (Edge e : adj[cure.to]) if (!vis[e.to]) que.add(e);
        }

        if (nvis != N) throw new RuntimeException("BidirectionalGraph isnt connected");
        return ret;
    }

    public BidirectionalGraph tree() {
        if (needUpd) {
            needUpd = false;
            recreateAdj();
        }

        BidirectionalGraph ret = new BidirectionalGraph(N);
        boolean[] vis = new boolean[N];
        vis[0] = true;
        int nvis = 1;
        PriorityQueue<Edge> que = new PriorityQueue<Edge>();
        for (Edge e : adj[0]) que.add(e);
        while (true) {
            if (nvis == N) break;
            if (que.isEmpty()) break;
            Edge cure = que.poll();
            if (vis[cure.to]) continue;
            vis[cure.to] = true;
            ret.addEdge(cure.from, cure.to, cure.cost);
            ret.addEdge(cure.to, cure.from, cure.cost);
            nvis++;
            for (Edge e : adj[cure.to]) if (!vis[e.to]) que.add(e);
        }

        if (nvis != N) throw new RuntimeException("BidirectionalGraph isnt connected");
        return ret;
    }
}
