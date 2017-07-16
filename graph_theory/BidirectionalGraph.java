package template.graph_theory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by dy on 16-12-1.
 * An adjacent list implementation of graph.
 */
public class BidirectionalGraph {

    protected int N, M;
    protected List<AbstractEdge>[] adj;
    private int[] indegree;
    private int[] outdegree;

    public BidirectionalGraph(){};

    public BidirectionalGraph(int N) {
        this.N = N;
        adj = new List[N];
        for (int i = 0; i < N; ++i) adj[i] = new ArrayList<>();
        indegree = new int[N];
        outdegree = new int[N];
    }

    public void addEdge (int from, int to, int capacity, int cost) {
        addEdge(new AbstractEdge() {
            @Override
            public int getCost() {
                return cost;
            }

            @Override
            public int getCapacity() {
                return capacity;
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

    public void addEdge (int from, int to, int cost) {
        addEdge(new AbstractEdge() {
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

    public void addEdge(AbstractEdge e) {
        int from = e.getFrom(), to = e.getTo();
        adj[from].add(e);
        M++;
        indegree[to]++;
        outdegree[from]++;
    }

    public List<AbstractEdge> adj(int node) {
        return adj[node];
    }

    public int indegree(int v) { return indegree[v];}
    public int outdegree(int v) { return outdegree[v];}

    public int E() {return M;};
    public int V() {return N;};


    public void sortEdge(Comparator<AbstractEdge> comparator) {
        for (int i = 0; i < N; ++i) Collections.sort(adj[i], comparator);
    }

    public void sortEdge() {
        for (int i = 0; i < N; ++i) {
            int from = i;
            Collections.sort(adj[i], new Comparator<AbstractEdge>() {
                @Override
                public int compare(AbstractEdge o1, AbstractEdge o2) {
                    return o1.other(from) - o2.other(from);
                }
            });
        }
    }

    public boolean hasEulerianCycle() {
        throw new UnsupportedOperationException();
    }

    public List<Integer> eulerianCycle() {
        throw new UnsupportedOperationException();
    }

    public boolean hasEulerainPath() {
        throw new UnsupportedOperationException();
    }

    public List<Integer> eulerianPath() {
        throw new UnsupportedOperationException();
    }

    public int minCycle() {
        return minCycle(null);
    }

    /**
     * Additionally check one or two node cycle.
     * @param cycle
     * @return
     */
    public int minCycle(List<Integer> cycle) {
        throw new UnsupportedOperationException();
    }

    public boolean hasNegativeCycle() {
        int n = N;
        int[][] dist = new int[n][n];
        int oo = Integer.MAX_VALUE;
        for (int i = 0; i < n; ++i) Arrays.fill(dist[i], oo);
        for (int i = 0; i < n; ++i) for (AbstractEdge e : adj(i)) {
            dist[i][e.other(i)] = e.getCost();
        }
        for (int k = 0; k < n; ++k)
            for (int i = 0; i < n; ++i)
                for (int j = 0; j < n; ++j) {
                    if (dist[i][k] != oo && dist[k][j] != oo && dist[i][j] > dist[i][k] + dist[k][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                        if (i == j && dist[i][j] < 0) return true;
                    }
                }
        return false;
    }

    public boolean isSparseGraph() {
        return N <= 1 || (double)M / N / (N - 1) < 0.45;
    }

    public boolean isPlanarGraph() {
        throw new UnsupportedOperationException();
    }

    public boolean directional() {
        return true;
    }

    public boolean isTree() {
        boolean[] visited = new boolean[N];
        return isTree(0, -1, visited);
    }

    private boolean isTree(int cur, int fa, boolean[] visited) {
        if (visited[cur]) return false;
        visited[cur] = true;
        for (AbstractEdge e : adj[cur]) {
            int to = e.getTo();
            if (!isTree(to, cur, visited)) return false;
        }
        return true;
    }

    private boolean isDAG() {
        int[] vis = new int[N];
        return isDAG(0, -1, vis);
    }

    private boolean isDAG(int cur, int fa, int[] vis) {
        if (vis[cur] == -1) return false;
        if (vis[cur] ==  1) return true;
        vis[cur] = -1;
        for (AbstractEdge e : adj[cur]) {
            int to = e.getTo();
            if (!isDAG(to, cur, vis)) return false;
        }
        vis[cur] = 1;
        return true;
    }

    @Override
    public String toString() {
        StringBuffer res = new StringBuffer(N + " " + M + "\n");
        for (int i = 0; i < N; ++i)
            for (AbstractEdge e : adj[i]) res.append(i + " " + e.other(i)+ " " + e.getCost() + "\n");
        return res.toString();
    }

    private String getEdgeStyle() {
        return "->";
    }

    private String getGraphStyle() {
        return "digraph";
    }

    public void show() {
//        this.w = (w + 99) / 100;
//        this.h = (h + 99) / 100;
        //type: dag, tree, null(else)
        StringBuffer dot = new StringBuffer(getGraphStyle() + " TMP {");
        String title;

        if (isTree()) {
            title = "TREE";
            boolean[] vis = new boolean[N];
            generateDot(vis, dot);
        } else {
            title = isDAG() ? "DAG" : "GRAPH";
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


    protected void generateDot(boolean[] vis, StringBuffer dot) {
        for (int i = 0; i < vis.length; ++i) if (!vis[i]) generateDot(i, vis, dot);
    }

    private void generateDot(int cur, boolean[] vis, StringBuffer dot) {
        if (vis[cur]) return;
        vis[cur] = true;

        for (AbstractEdge e : adj[cur]) {
            int to = e.getTo();
            dot.append(cur).append(getEdgeStyle()).append(to);
            if (e.toString() != null) dot.append(" [label=\"").append(e.toString()).append("\"];");
            else dot.append(';');
            generateDot(to, vis, dot);
        }
    }

    protected void showDot(String title, StringBuffer dot) {
        try {
            File input = File.createTempFile(title, ".dot");
            File output = File.createTempFile(title, ".pdf");
            PrintWriter dotWriter = new PrintWriter(new FileWriter(input));
            dotWriter.println(dot.toString());
            dotWriter.close();
            String dotCommd = "dot " + input.getAbsolutePath() + " -Tpdf -o " + output.getAbsolutePath();
            Runtime.getRuntime().exec(new String[] { "bash", "-c", dotCommd}).waitFor();
            Runtime.getRuntime().exec(new String[] { "bash", "-c", "open " + output.getAbsolutePath()}).waitFor();
            Thread.sleep(500);

            input.deleteOnExit();
            output.deleteOnExit();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        Draw draw = new Draw(title);
//        draw.setCanvasSize(w * 100, h * 100);
//        draw.picture(.5, .5, tmp.getAbsolutePath());
        //while (!tmp.delete());
    }

    public static void main(String[] args) {
        BidirectionalGraph g = new BidirectionalGraph(3);
        g.addEdge(0, 1, 1);
        g.addEdge(0, 2, 1);

        g.show();
    }

}
