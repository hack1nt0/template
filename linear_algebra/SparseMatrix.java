package template.linear_algebra;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by dy on 17-1-15.
 */
public class SparseMatrix implements Iterable<int[]> {
    Map<Integer, Integer>[] matrixR;
    Map<Integer, Integer>[] matrixC;
    int N, M;

    public SparseMatrix(int N, int M) {
        this.N = N;
        this.M = M;
        matrixR = new HashMap[N];
        matrixC = new HashMap[M];
    }

    public void put(int r, int c, int v) {
        if (matrixR[r] == null) matrixR[r] = new HashMap<Integer, Integer>();
        matrixR[r].put(c, v);
        if (matrixC[c] == null) matrixC[c] = new HashMap<Integer, Integer>();
        matrixC[c].put(r, v);
    }

    public long get(int r, int c) {
        return matrixR[r].get(c);
    }

    public Map<Integer, Integer> getR(int r) {
        return matrixR[r];
    }

    public boolean existR(int r) {
        return matrixR[r] != null && matrixR[r].size() > 0;
    }

    public boolean exist(int r, int c) {
        if (matrixR[r] == null) return false;
        return matrixR[r].containsKey(c);
    }

    public static SparseMatrix multiply(SparseMatrix A, SparseMatrix B) {
        int N = A.N, M = B.M;
        SparseMatrix C = new SparseMatrix(N, M);
        for (int i = 0; i < N; ++i) {
            if (A.matrixR[i] == null) continue;
            for (int j = 0; j < M; ++j) {
                if (B.matrixC[j] == null) continue;
                int cijv = 0;
                if (A.matrixR[i].size() < B.matrixC[j].size()) {
//                    for (Map.Entry<Integer, Long> ake : A.matrixR[i].entrySet()) {
//                        int ak = ake.getKey();
//                        Long bkv = B.matrixC[j].get(ak);
//                        if (bkv == null) continue;
//                        long akv = ake.getFromValue();
//                        cijv += akv * bkv;
//                    }
                    for (int ak : A.matrixR[i].keySet()) {
                        if (!B.matrixC[j].containsKey(ak)) continue;
                        cijv += A.matrixR[i].get(ak) * B.matrixC[j].get(ak);
                    }
                } else {
                    for (int bk : B.matrixC[j].keySet()) {
                        if (!A.matrixR[i].containsKey(bk)) continue;
                        cijv += A.matrixR[i].get(bk) * B.matrixC[j].get(bk);
                    }
                }
                C.put(i, j, cijv);
            }
        }
        return C;
    }

    @Override
    public String toString() {
        return "SparseMatrix{" +
                "matrixR=" + Arrays.toString(matrixR) +
                ", matrixC=" + Arrays.toString(matrixC) +
                ", N=" + N +
                ", M=" + M +
                '}';
    }

    public Iterator<int[]> iterator() {
        return new Iterator<int[]>() {
            int cr = 0;
            Iterator<Map.Entry<Integer, Integer>> innnerIter;
            public boolean hasNext() {
                while (true) {
                    if (innnerIter == null) {
                        while (cr < N && !existR(cr)) cr++;
                        if (cr >= N) return false;
                        innnerIter = matrixR[cr].entrySet().iterator();
                    }
                    if (innnerIter.hasNext()) return true;
                    else {
                        innnerIter = null;
                        cr++;
                    }
                }
            }

            public int[] next() {
                Map.Entry<Integer, Integer> cur = innnerIter.next();
                return new int[]{cr, cur.getKey(), cur.getValue()};
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
