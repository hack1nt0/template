package template.graph_theory;

import java.util.Arrays;
import java.util.Stack;

/**
 * Created by dy on 16-12-20.
 */
public class CCTarjan {
    //public Set<Integer>[] adj;
    private BidirectionalGraph graph, dag;
    int ncc, nscc, N;
    private boolean[] marked;        // marked[v] = has v been visited?
    private int[] whichSCC;                // id[v] = id of strong connected component containing v
    private int[] whichCC;                // id[v] = id of connected component containing v
    private int[] low;               // low[v] = low number of v
    private int pre;                 // preorder number counter
    private Stack<Integer> stack;

    public CCTarjan(BidirectionalGraph graph) {
        this.graph = graph;
        this.N = graph.V();
        marked = new boolean[N];
        stack = new Stack<Integer>();
        whichSCC = new int[N];
        low = new int[N];
        ncc = nscc = -1;
        if (graph instanceof UndirectionalGraph) cc();
        else scc();
    }

    private void cc() {
        if (graph.directional()) throw new UnsupportedOperationException();
        int N = graph.V();
        whichCC = new int[N];
        Arrays.fill(whichCC, -1);
        ncc = 0;
        for (int i = 0; i < N; ++i) if (whichCC[i] == -1)
            cchelper(i, whichCC, ncc++);
    }

    public int[] whichCC() {
        if (graph.directional()) throw new UnsupportedOperationException();
        return whichCC;
    }

    public int nCC() {
        if (graph.directional()) throw new UnsupportedOperationException();
        return ncc;
    }

    private void cchelper(int cur, int[] idcc, int ncc) {
        idcc[cur] = ncc;
        for (AbstractEdge e : graph.adj(cur)) {
            int chd = e.other(cur);
            if (idcc[chd] == -1)
                cchelper(chd, idcc, ncc);
        }
    }

    private void scc() {
        if (!graph.directional()) throw new UnsupportedOperationException();
        pre = 0;
        nscc = 0;
        Arrays.fill(marked, false);
        for (int v = 0; v < N; v++) if (!marked[v])
            dfs(v);
    }

    private void dfs(int v) {
        marked[v] = true;
        low[v] = pre++;
        int min = low[v];
        stack.push(v);
        for (AbstractEdge e : graph.adj[v]) {
            int w = e.other(v);
            if (!marked[w]) dfs(w);
            if (low[w] < min) min = low[w];
        }
        if (min < low[v]) {
            low[v] = min;
            return;
        }
        int w;
        do {
            w = stack.pop();
            whichSCC[w] = nscc;
            low[w] = N;
        } while (w != v);
        nscc++;
    }

    public int[] whichSCC() {
        if (!graph.directional()) throw new UnsupportedOperationException();
        return whichSCC;
    }

    public int nSCC() {
        if (!graph.directional()) throw new UnsupportedOperationException();
        return nscc;
    }

    public BidirectionalGraph dag() {
        if (!graph.directional()) throw new UnsupportedOperationException();
        if (dag == null) {
            boolean[][] visited = new boolean[nscc][nscc];
            dag = new BidirectionalGraph(nscc);
            for (int i = 0; i < graph.V(); ++i) {
                for (AbstractEdge e : graph.adj(i)) {
                    int j = e.other(i);
                    if (whichSCC[i] == whichSCC[j] || visited[whichSCC[i]][whichSCC[j]]) continue;
                    visited[whichSCC[i]][whichSCC[j]] = true;
                    dag.addEdge(whichSCC[i], whichSCC[j]);
                }
            }
        }
        return dag;
    }

    public static void main(String[] args) {
    }
}
