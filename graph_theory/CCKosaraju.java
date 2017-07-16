package template.graph_theory;

import java.util.*;

/**
 * Created by dy on 16-12-20.
 */
public class CCKosaraju {
    public Set<Integer>[] adj;
    private Set<Integer>[] iadj;
    int ncc, nscc;

    public CCKosaraju(int N) {
        adj = new HashSet[N];
        for (int i = 0; i < N; ++i) adj[i] = new HashSet<Integer>();
        iadj = new HashSet[N];
        for (int i = 0; i < N; ++i) iadj[i] = new HashSet<Integer>();
    }

    public void addE(int a, int b) {
        //if (from == to) throw new RuntimeException();
        adj[a].add(b);
        iadj[b].add(a);
    }

    public void removeE(int a, int b) {
        adj[a].remove(b);
        iadj[b].remove(a);
    }

    public int[] cc() {
        int N = adj.length;
        int[] idcc = new int[N];
        Arrays.fill(idcc, -1);
        ncc = 0;
        for (int i = 0; i < N; ++i) if (idcc[i] == -1)
            cchelper(i, idcc, ncc++, adj);
        return idcc;
    }

    public int ncc() {
        cc();
        return ncc;
    }

    private void cchelper(int cur, int[] idcc, int ncc, Set<Integer>[] adj) {
        idcc[cur] = ncc;
        for (int chd : adj[cur]) if (idcc[chd] == -1)
            cchelper(chd, idcc, ncc, adj);
    }

    public int[] scc() {
        int N = adj.length;
        final int[] postOrder = postFirstSearch();
        Integer[] id = new Integer[N];
        for (int i = 0; i < N; ++i) id[i] = i;
        int[] idscc = new int[N];
        Arrays.fill(idscc, -1);
        Arrays.sort(id, new Comparator<Integer>() {
            public int compare(Integer o1, Integer o2) {
                return postOrder[o2] - postOrder[o1];
            }
        });
        for (int i = 0; i < N; ++i) if (idscc[id[i]] == -1)
            cchelper(id[i], idscc, nscc++, iadj);

        return idscc;
    }

    public int nscc() {
        scc();
        return nscc;
    }

    private int npost;
    private int[] postFirstSearch() {
        int N = adj.length;
        int[] vis = new int[N];
        int[] postOrder = new int[N];
        npost = 0;
        for (int i = 0; i < N; ++i) if (vis[i] == 0)
            postFirstSearchHelper(i, vis, postOrder);
        return postOrder;
    }

    private void postFirstSearchHelper(int cur, int[] vis, int[] postOrder) {
        if (vis[cur] != 0) return;
        vis[cur] = -1;
        for (int chd : adj[cur]) postFirstSearchHelper(chd, vis, postOrder);
        vis[cur] = 1;
        postOrder[cur] = npost++;
    }

    public static void main(String[] args) {
        CCKosaraju cc = new CCKosaraju(3);
        cc.addE(0, 1);
        cc.addE(1, 2);
        System.out.println("nscc " + cc.nscc());
        System.out.println("ncc " + cc.ncc());
    }
}
