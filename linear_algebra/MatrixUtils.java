package template.linear_algebra;


/**
 * Created by dy on 16-10-8.
 */
public class MatrixUtils {
    // applied to count problem, where every element of Square is positive.
    // there may be overflows, or we just need from the sum of some matrix satisfy
    // some from lower bound.

    public static long[][] multiply(long[][] A, long[][] B) {
        if (A == null || B == null) return null;
        if (A[0].length != B.length) throw new RuntimeException();
        long[][] res = new long[A.length][B[0].length];
        for (int i = 0; i < A.length; ++i)
            for (int j = 0; j < B[0].length; ++j) {
                res[i][j] = 0;
                for (int k = 0; k < B.length; ++k) {
                    res[i][j] += A[i][k] * B[k][j];
                }
            }
        return res;
    }

    public static void multiply(double[][] A, double[][] B) {
        int N = A.length;
        int M = B[0].length;
        int K = A[0].length;
        double[][] res = new double[N][M];
        assert B.length == K;
        for (int i = 0; i < M; ++i)
            for (int j = 0; j < N; ++j)
                for (int k = 0; k < K; ++k)
                    res[i][j] += A[i][k] * B[k][j];
    }

//    public static void multiplyX(DenseMatrix A, DenseMatrix B, DenseMatrix res, boolean transA, boolean transB) {
//        if (transA && transB) A.transABmult(B, res);
//        else if (transA) A.transAmult(B, res);
//        else if (transB) A.transBmult(B, res);
//        else A.mult(B, res);
//    }

//    public static DenseMatrix multiplyX(DenseMatrix A, DenseMatrix B) {
//        int N = A.numRows();
//        int M = B.numColumns();
//        DenseMatrix res = new DenseMatrix(N, M);
//        A.mult(B, res);
//        return res;
//    }

    public static long[][] pow(long[][] M, long k) {
        if (M.length != M[0].length) throw new RuntimeException();
        if (k < 0) throw new RuntimeException();

        long[][] res = new long[M.length][M.length];
        for (int i = 0; i < res.length; ++i) res[i][i] = 1;
        long[][] acc = M;
        while (k > 0) {
            if ((k & 1) > 0) res = multiply(res, acc);
            acc = multiply(acc, acc);
            k >>= 1;
        }
        return res;
    }

    public static long[][] add(long[][] A, long[][] B) {
        if (A == null || B == null) return null;
        if (A.length != B.length || A[0].length != B[0].length) throw new RuntimeException();

        long[][] res = new long[A.length][A[0].length];
        for (int i = 0; i < A.length; ++i)
            for (int j = 0; j < A[0].length; ++j) {
                res[i][j] = A[i][j] + B[i][j];
            }
        return res;
    }

    public static long sum(long[][] A) {
        Long res = new Long(0);
        for (int i = 0; i < A.length; ++i)
            for (int j = 0; j < A[i].length; ++j) res += A[i][j];
        return res;
    }

    public static int[][] rotate90Right(int[][] m) {
        int h = m.length, w = m[0].length;
        int[][] res = new int[w][h];
        for (int i = 0; i < w; ++i) for (int j = 0; j < h; ++j) res[i][j] = m[j][i];
        int lj = 0, rj = h - 1;
        while (lj < rj) {
            for (int i = 0; i < w; ++i) {
                int swap = res[i][lj];
                res[i][lj] = res[i][rj];
                res[i][rj] = swap;
            }
            lj++;
            rj--;
        }
        return res;
    }

    public static int[][] mirrorHorizontally(int[][] m) {
        int h = m.length, w = m[0].length;
        int[][] res = new int[h][w];
        for (int i = 0; i < h; ++i) for (int j = 0; j < w; ++j) res[i][j] = m[i][j];
        int li = 0, ri = h - 1;
        while (li < ri) {
            for (int j = 0; j < w; ++j) {
                int swap = res[li][j];
                res[li][j] = res[ri][j];
                res[ri][j] = swap;
            }
            li++;
            ri--;
        }
        return res;
    }

    public static int[][] mirrorVertically(int[][] m) {
        int h = m.length, w = m[0].length;
        int[][] res = new int[h][w];
        for (int i = 0; i < h; ++i) for (int j = 0; j < w; ++j) res[i][j] = m[i][j];
        int lj = 0, rj = w - 1;
        while (lj < rj) {
            for (int i = 0; i < h; ++i) {
                int swap = res[i][lj];
                res[i][lj] = res[i][rj];
                res[i][rj] = swap;
            }
            lj++;
            rj--;
        }
        return res;
    }
}
