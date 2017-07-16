package template.operation;

import template.collection.sequence.IntBinaryHeap;
import template.collection.IntArrayList;
import template.graph_theory.AbstractEdge;
import template.graph_theory.BidirectionalGraph;

import java.util.*;

/**
 * Created by dy on 16-12-1.
 */
public class ShortestPath {

    int N, M;
    public HashMap<Integer, AbstractEdge> [] adj;
    
    public final Integer INF = Integer.MAX_VALUE;

    public ShortestPath(int N) {
        this.N = N;
        adj = new HashMap[N];
        for (int i = 0; i < N; ++i) adj[i] = new HashMap<>();
    }

    public boolean addE (int from, int to, int cost) {
        if (from == to) {
            if (cost < 0) {
                //throw new RuntimeException("Negative loops exist.");
                return false;
            }
        } else {
            if (dist(from, to) > cost) adj[from].put(to, new AbstractEdge() {
                @Override
                public int getCost() {
                    return cost;
                }
            });
            M++;
        }
        return true;
    }

    public boolean addE (AbstractEdge abstractEdge) {
        int from = abstractEdge.getFrom();
        int to = abstractEdge.getTo();
        long cost = abstractEdge.getCost();
        if (from == to) {
            if (cost < 0) {
                //throw new RuntimeException("Negative loops exist.");
                return false;
            }
        } else {
            if (dist(from, to) > cost) adj[from].put(to, abstractEdge);
            M++;
        }
        return true;
    }



    public long[] bfs(int S) {
        long[] d = new long[N];
        Arrays.fill(d, INF);
        d[S] = 0;
        Queue<Integer> que = new LinkedList<Integer>();
        que.add(S);
        while (!que.isEmpty()) {
            int a = que.poll();
            for (int b : adj[a].keySet()) {
                long a2b = d[a] + dist(a, b);
                if (a2b < d[b]) {
                    d[b] = a2b;
                    que.add(b);
                }
            }
        }

        return d;
    }

    //bellman-ford
    public long[] bellmanford(int S) {
        long[] d = new long[N];
        Arrays.fill(d, INF);
        boolean[] inque = new boolean[N];
        d[S] = 0;
        //Queue<Integer> que = new LinkedList<Integer>();
        IntArrayList que = new IntArrayList(N);
        que.add(S);
        while (true) {
            if (que.isEmpty()) break;
            int a = que.poll();
            inque[a] = false;
            for (int b : adj[a].keySet()) {
                long a2b = d[a] + dist(a, b);
                if (a2b >= d[b]) continue;
                d[b] = a2b;
                if (!inque[b]) {
                    que.add(b);
                    inque[b] = true;
                }
            }
        }
        return d;
    }

    public long[] dijkstra(int S) {
        return dijkstra(S, -1);
    }

    public long[] dijkstra(int S, int T) {
        final long[] d = new long[N];
        boolean[] fixed = new boolean[N];
        Arrays.fill(d, INF);
        //PriorityQueue<Tuple2<Integer, Long>> pq = new PriorityQueue<>(Tuple2.SENCOND_ELEMENT_ORDER);
        IntBinaryHeap pq = new IntBinaryHeap(N, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                if (d[o1] > d[o2]) return +1;
                if (d[o1] < d[o2]) return -1;
                return 0;
            }
        });
        d[S] = 0;
        for (int i = 0; i < N; ++i) pq.add(i);

        while (true) {
            if (pq.isEmpty()) break;
            int from = pq.poll();
            if (fixed[from]) {
                throw new RuntimeException();
            }
            fixed[from] = true;
            if (d[from] == INF) {
                throw new RuntimeException();
            }
            //long cost = d[from];
            //d[from] = cost;
            if (from == T) break;

            for (int to : adj[from].keySet()) {
                if (fixed[to]) continue;
                long from2To = dist(from, to);
                if (from2To == INF) continue;
                long start2to = d[from] + from2To;
                if (start2to < d[to]) {
                    d[to] = start2to;
                    pq.adjust(to);
                }
            }
        }
        return d;
    }

    public long[][] floyd() {
        long[][] ret = new long[N][N];
        for (int i = 0; i < N; ++i)
            for (int j = 0; j < N; ++j) ret[i][j] = dist(i, j);
        for (int k = 0; k < N; ++k)
            for (int i = 0; i < N; ++i)
                for (int j = 0; j < N; ++j) {
                    if (ret[i][k] != INF && ret[k][j] != INF) {
                        ret[i][j] = Math.min(ret[i][j], ret[i][k] + ret[k][j]);
                    }
                }
        return ret;
    }

    public BidirectionalGraph graph() {
        // TODO: 16-12-11
        return null;
    }

    public BidirectionalGraph minPath(int S, int T) {
        // TODO: 16-12-11

        return null;
    }

    /**
     * @param S
     * @return Null if contains negative cycle, d[] else wise.
     */
    public long[] hasNegativeCycle(int S) {
        long[] d = new long[N];
        for (int i = 0; i < N; ++i) {
            boolean updated = false;
            for (int from = 0; from < N; ++from) {
                for (int to : adj[from].keySet()) {
                    long ndist = d[from] + dist(from, to);
                    if (ndist < d[to]) {
                        d[to] = ndist;
                        updated = true;
                    }
                }
            }
            if (updated && i == N - 1) return null;
        }
        return d;
    }

    public List<List<Integer>> negativeCycles(){
        // TODO: 2017/2/3 How to hashing the cycles?
        return null;
    }

    public int dist(int from, int to) {
        if (from == to) return 0;
        return adj[from].containsKey(to) ? adj[from].get(to).getCost() : INF;
    }

    public long minCycle() {
        return minCycle(null);
    }

    /**
     * Return the minimum cycle of all there-or-more-node cycles.
     * In the form of " from -> a -> b -> ... -> from "
     * @param cycle
     * @return length of minimum cycle
     */
    public long minCycle(List<Integer> cycle) {
        int[][] next = null;
        if (cycle != null) {
            next = new int[N][N];
            //for (int i = 0; i < N; ++i) Arrays.fill(next[i], i);
        }
        long[][] minDist = new long[N][N];
        long res = INF;
        for (int i = 0; i < N; ++i)
            for (int j = 0; j < N; ++j) {
                minDist[i][j] = dist(i, j);
                if (minDist[i][j] != INF && cycle != null) next[i][j] = j;
            }
        for (int k = 0; k < N; ++k)
            for (int i = 0; i < N; ++i)
                for (int j = 0; j < N; ++j) {
                    if (i < j && j < k && minDist[i][j] != INF && dist(j, k) != INF && dist(k, i) != INF) {
                        long curCycleLen = minDist[i][j] + dist(j, k) + dist(k, i);
                        if (curCycleLen < res) {
                            res = curCycleLen;
                            if (cycle != null) {
                                cycle.clear();
                                int t = i;
                                while (true) {
                                    cycle.add(t);
                                    if (t == j) break;
                                    t = next[t][j];
                                }
                                cycle.add(k);
                                cycle.add(i);
                            }
                        }
                    }
                    if (minDist[i][k] != INF && minDist[k][j] != INF) {
                        long curDist = minDist[i][k] + minDist[k][j];
                        if (curDist < minDist[i][j]) {
                            minDist[i][j] = curDist;
                            if (cycle != null) {
                                next[i][j] = next[i][k];
                            }
                        }
                    }

                }
        return res;
    }

    public static void main(String[] args) {
        testCycle();
    }

    public static void testCycle() {
        ShortestPath shortestPath = new ShortestPath(3);
        shortestPath.addE(0, 1, 2);
        shortestPath.addE(1, 2, 2);
        shortestPath.addE(2, 0, 2);
        List<Integer> cycle = new ArrayList<>();
        System.out.println(shortestPath.minCycle(cycle));
        System.out.println(Arrays.toString(cycle.toArray()));

    }

}
