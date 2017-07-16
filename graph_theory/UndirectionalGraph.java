package template.graph_theory;

import template.collection.sequence.ArrayQueue;
import template.collection.IntArrayList;

import java.util.*;

/**
 * @author dy[jealousing@gmail.com] on 17-2-11.
 */
public class UndirectionalGraph extends BidirectionalGraph {

    private int[] degree;

    public UndirectionalGraph(int N) {
        this.N = N;
        adj = new List[N];
        for (int i = 0; i < N; ++i) adj[i] = new ArrayList<>();
        degree = new int[N];
    }

    public void addEdge (int from, int to, int cost) {
        addEdge(new AbstractEdge() {
            boolean visited = false;
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

        });
    }

    public void addEdge (int from, int to) {
        addEdge(new AbstractEdge() {
            @Override
            public int getFrom() {
                return from;
            }

            @Override
            public int getTo() {
                return to;
            }
        });
    }

    @Override
    public void addEdge(AbstractEdge e) {
        int from = e.getFrom(), to = e.getTo();
        adj[from].add(e);
        adj[to].add(e);
        M++;
        degree[from]++;
        degree[to]++;
    }

    public int degree(int v) {
        return degree[v];
    }

    public boolean hasCycle() {
        for (int v = 0; v < N; v++) {
            int d = degree(v);
            if (d % 2 != 0) return false;
        }
        return true;
    }

    public List<Integer> cycle() {
        List<Integer> cycle = new ArrayList<>();
        if (M == 0) return cycle;

        // necessary condition: all vertices have even degree
        // (this test is needed or it might find an Eulerian path instead of cycle)
        int start = -1;
        for (int v = N - 1; v >= 0; --v) {
            int d = degree(v);
            if (d % 2 != 0) return null;
            if (d > 0) start = v;
        }
        if (start == -1) throw new IllegalArgumentException();

        // initialize stack with any non-isolated vertex
        IntArrayList stack = new IntArrayList();
        stack.push(start);

        // greedily search through edges in iterative DFS style
        for (int i = 0; i < N; ++i) for (AbstractEdge e : adj[i]) e.notVisited();
        cycle = new ArrayList<>();
        int[] edgeIndex = new int[N];
        while (!stack.isEmpty()) {
            int v = stack.pop();
            while (edgeIndex[v] < adj[v].size()) {
                AbstractEdge edge = adj[v].get(edgeIndex[v]++);
                if (edge.getVisited()) continue;
                edge.setVisited();
                stack.push(v);
                v = edge.other(v);
            }
            // push vertex with no more leaving edges to cycle
            cycle.add(v);
        }

        // check if all edges are used
        if (cycle.size() != M + 1) throw new RuntimeException();

        return cycle;
    }

    public boolean hasPath() {
        int odd = 0;
        for (int v = 0; v < N; v++) {
            int d = degree(v);
            if (d % 2 != 0) odd++;
        }
        return odd == 2;
    }

    public List<Integer> path() {
        int odd = 0;
        int start = -1;
        //for (int v = 0; v < N; ++v) {
        for (int v = N - 1; v >= 0; --v) {
            if (degree(v) % 2 != 0) {
                odd++;
                start = v;
            }
        }
        if (!(odd == 2)) return null;

        List<Integer> cycle = new ArrayList<>();
        // initialize stack with any non-isolated vertex
        IntArrayList stack = new IntArrayList();
        stack.push(start);

        // greedily search through edges in iterative DFS style
        for (int i = 0; i < N; ++i) for (AbstractEdge e : adj[i]) e.notVisited();
        int[] edgeIndex = new int[N];
        while (!stack.isEmpty()) {
            int v = stack.pop();
            while (edgeIndex[v] < adj[v].size()) {
                AbstractEdge edge = adj[v].get(edgeIndex[v]++);
                if (edge.getVisited()) continue;
                edge.setVisited();
                stack.push(v);
                v = edge.other(v);
            }
            // push vertex with no more leaving edges to cycle
            cycle.add(v);
        }

        // check if all edges are used
        if (cycle.size() != M + 1) throw new RuntimeException();

        return cycle;
    }

    public boolean[] cut() {
        boolean[] isCut = new boolean[N];
        int[] low = new int[N];
        int[] depth = new int[N];
        Arrays.fill(depth, -1);
        depth[0] = 0;
        cut(0, -1, isCut, depth, low);
        return isCut;
    }

    private void cut(int cur, int fa, boolean[] isCut, int[] depth, int[] low) {
        int curLow = depth[cur];
        int chds = 0;
        for (AbstractEdge e : adj[cur]) {
            int chd = e.other(cur);
            if (depth[chd] == -1) {
                chds++;
                depth[chd] = depth[cur] + 1;
                cut(chd, cur, isCut, depth, low);
                if (low[chd] >= depth[cur]) isCut[cur] = true;
                curLow = Math.min(curLow, low[chd]);
            } else if (chd != fa) {
                curLow = Math.min(curLow, depth[chd]);
            }
        }
        low[cur] = curLow;
        if (cur == 0) isCut[cur] = chds >= 2;
    }

    /**
     * bi-connected(node) component
     * For example, two nodes with a single edge(actually two directional edge) is a BCC.
     */
    public List<Set<Integer>> bcc() {
        List<Set<Integer>> bccList = new ArrayList<>();
        ArrayQueue<AbstractEdge> stack = new ArrayQueue<>();
        int[] low = new int[N];
        int[] depth = new int[N];
        Arrays.fill(depth, -1);
        depth[0] = 0;
        bcc(0, -1, depth, low, stack, bccList);
        return bccList;
    }

    private void bcc(int cur, int fa, int[] depth, int[] low, ArrayQueue<AbstractEdge> stack, List<Set<Integer>> bccList) {
        int curLow = depth[cur];
        int chds = 0;

        for (AbstractEdge e : adj[cur]) {
            int chd = e.other(cur);
            if (depth[chd] == -1) {
                chds++;
                stack.push(e);
                depth[chd] = depth[cur] + 1;
                bcc(chd, cur, depth, low, stack, bccList);
                if (low[chd] >= depth[cur]) {
                    //isCut[cur] = true;
                    Set<Integer> bcc = new HashSet<>();
                    while (true) {
                        AbstractEdge cure = stack.pop();
                        int from = cure.getFrom(), to = cure.getTo();
                        bcc.add(from); bcc.add(to);
                        if (from == cur && to == chd) break;
                    }
                    bccList.add(bcc);
                }
                curLow = Math.min(curLow, low[chd]);
            } else if (chd != fa) {
                //stack.push(e); //should not be added.
                curLow = Math.min(curLow, depth[chd]);
            }
        }
        low[cur] = curLow;
    }

    @Override
    public boolean directional() {
        return false;
    }

    public boolean isTree() {
        boolean[] visited = new boolean[N];
        return isTree(0, -1, visited);
    }

    private boolean isTree(int cur, int fa, boolean[] visited) {
        if (visited[cur]) return false;
        visited[cur] = true;
        for (AbstractEdge e : adj[cur]) {
            int to = e.other(cur);
            if (to == fa) continue;
            if (!isTree(to, cur, visited)) return false;
        }
        return true;
    }

    public boolean isBipartite() {
        return false;
    }

    private String getEdgeStyle() {
        return "--";
    }

    private String getGraphStyle() {
        return "graph";
    }

    public void show() {
        StringBuffer dot = new StringBuffer(getGraphStyle() + " TMP {");
        String title;

        for (int i = 0; i < N; ++i) for (AbstractEdge e : adj[i]) e.notVisited();

        if (isTree()) {
            title = "TREE";
            boolean[] vis = new boolean[N];
            generateDot(vis, dot);
        } else {
            title = isBipartite() ? "BIPARTITE" : "GRAPH";
            dot.append("rankdir=LR;\n")
                    .append("node[group=main];\n");
            boolean[] vis = new boolean[N];
            generateDot(vis, dot);
        }
        for (int node = 0; node < N; ++node) dot.append(String.valueOf(node)).append(";");
        dot.append("}");
        showDot(title, dot);
    }

//    private void showTree(int cur, int fa, StringBuffer dot) {
//        for (AbstractEdge e : adj[cur]) {
//            if (e.getTo() == fa) continue;
//            dot.append(cur).append(getEdgeStyle()).append(e.getTo()).append(';');
//        }
//        for (AbstractEdge e : adj[cur]) {
//            if (e.getTo() == fa) continue;
//            showTree(e.getTo(), cur, dot);
//        }
//    }

    private void generateDot(int cur, boolean[] vis, StringBuffer dot) {
        if (vis[cur]) return;
        vis[cur] = true;

        for (AbstractEdge e : adj[cur]) {
            if (e.getVisited()) continue;
            e.setVisited();
            int to = e.other(cur);
            dot.append(cur).append(getEdgeStyle()).append(to).append(';');
            generateDot(to, vis, dot);
        }
    }

    public static void main(String[] args) {
        testBcc();
    }

    private static void testBcc() {
        int N = 6;
        UndirectionalGraph graph = new UndirectionalGraph(N);
        graph.addEdge(0, 3);
        graph.addEdge(0, 1);
        graph.addEdge(3, 2);
        graph.addEdge(3, 3);
        graph.addEdge(3, 3);
        graph.addEdge(4, 5);

        graph.show();

        for (Set<Integer> bcc : graph.bcc()) System.out.println(bcc);
    }
}
