package template.collection.sequence;

import com.sun.javafx.image.IntToIntPixelConverter;
import template.collection.tuple.Tuple3;
import template.debug.InputReader;
import template.debug.RandomUtils;
import template.debug.Stopwatch;
import template.numbers.IntUtils;

import javax.management.relation.RoleUnresolved;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by dy on 16-12-22.
 *
 * Both Array and List. (The same in some extent)
 */
public class ArrayUtils {

    public static void swap(int[] arr, int i, int j) {
        int t = arr[i];
        arr[i] = arr[j];
        arr[j] = t;
    }

    public static int[] index(int n) {
        int[] res = new int[n];
        for (int i = 0; i < n; ++i) res[i] = i;
        return res;
    }

    public static int[] clone(int[] a) {
        if (a.length <= 0) throw new IllegalArgumentException();
        int[] res = new int[a.length];
        System.arraycopy(a, 0, res, 0, a.length);
        return res;
    }
    public static void fill(int[][] arr, int v) {for (int i = 0; i < arr.length; ++i) Arrays.fill(arr[i], v);}

    public static void fill(int[][][] arr, int v) {
        for (int i = 0; i < arr.length; ++i) fill(arr[i], v);
    }

    public static void fill(int[][][][] arr, int v) {
        for (int i = 0; i < arr.length; ++i) fill(arr[i], v);
    }

    public static void fill(int[][][][][] arr, int v) {
        for (int i = 0; i < arr.length; ++i) fill(arr[i], v);
    }

    public static void fill(long[][] arr, long v) {for (int i = 0; i < arr.length; ++i) Arrays.fill(arr[i], v);}

    public static void fill(long [][][] arr, long v) {
        for (int i = 0; i < arr.length; ++i) fill(arr[i], v);
    }

    public static void fill(long[][][][] arr, long v) {
        for (int i = 0; i < arr.length; ++i) fill(arr[i], v);
    }

    public static void fill(long[][][][][] arr, long v) {
        for (int i = 0; i < arr.length; ++i) fill(arr[i], v);
    }



    public static Object[] inbox(Object arr) {
        if (!(arr instanceof Object[] || arr.getClass().isArray())) {
            return new Object[]{arr};
        }
        if (arr instanceof Object[])
            return (Object[]) arr;

        if (arr instanceof int[]) {
            int[] tmp = (int[]) arr;
            int N = tmp.length;
            Integer[] res = new Integer[N];
            for (int i = 0; i < N; ++i) res[i] = tmp[i];
            //System.arraycopy(tmp, 0, res, 0, N);
            return res;
        }
        if (arr instanceof long[]) {
            long[] tmp = (long[]) arr;
            int N = tmp.length;
            Long[] res = new Long[N];
            for (int i = 0; i < N; ++i) res[i] = tmp[i];
            return res;
        }
        if (arr instanceof char[]) {
            char[] tmp = (char[]) arr;
            int N = tmp.length;
            Character[] res = new Character[N];
            for (int i = 0; i < N; ++i) res[i] = tmp[i];
            return res;
        }
        if (arr instanceof float[]) {
            float[] tmp = (float[]) arr;
            int N = tmp.length;
            Float[] res = new Float[N];
            for (int i = 0; i < N; ++i) res[i] = tmp[i];
            return res;
        }
        if (arr instanceof double[]) {
            double[] tmp = (double[]) arr;
            int N = tmp.length;
            Double[] res = new Double[N];
            for (int i = 0; i < N; ++i) res[i] = tmp[i];
            return res;
        }
        if (arr instanceof byte[]) {
            byte[] tmp = (byte[]) arr;
            int N = tmp.length;
            Byte[] res = new Byte[N];
            //System.arraycopy(tmp, 0, res, 0, N);
            for (int i = 0; i < N; ++i) res[i] = tmp[i];
            return res;
        }
        if (arr instanceof boolean[]) {
            boolean[] tmp = (boolean[]) arr;
            int N = tmp.length;
            Boolean[] res = new Boolean[N];
            System.arraycopy(tmp, 0, res, 0, N);
            return res;
        }

        return null;
    }

    public static Object outbox(Object arr) {
        boolean isObjectArray = arr instanceof Object[];
        boolean isPrimitiveArray = !isObjectArray && arr.getClass().isArray();
        if (isPrimitiveArray) return arr;

        int N = ((Object[])arr).length;
        if (arr instanceof Integer[]) {
            Integer[] tmp = (Integer[]) arr;
            int[] res = new int[N];
            for (int i = 0; i < N; ++i) res[i] = tmp[i];
            //System.arraycopy(tmp, 0, res, 0, N);
            return res;
        }
        if (arr instanceof Long[]) {
            Long[] tmp = (Long[])arr;
            long[] res = new long[N];
            for (int i = 0; i < N; ++i) res[i] = tmp[i];
            return res;
        }
        if (arr instanceof Character[]) {
            Character[] tmp = (Character[]) arr;
            char[] res = new char[N];
            for (int i = 0; i < N; ++i) res[i] = tmp[i];
            return res;
        }
        if (arr instanceof Float[]) {
            Float[] tmp = (Float[]) arr;
            float[] res = new float[N];
            for (int i = 0; i < N; ++i) res[i] = tmp[i];
            return res;
        }
        if (arr instanceof Double[]) {
            Double[] tmp = (Double[]) arr;
            double[] res = new double[N];
            for (int i = 0; i < N; ++i) res[i] = tmp[i];
            return res;
        }
        if (arr instanceof Byte[]) {
            Byte[] tmp = (Byte[]) arr;
            byte[] res = new byte[N];
            System.arraycopy(tmp, 0, res, 0, N);
            return res;
        }
        if (arr instanceof Boolean[]) {
            Boolean[] tmp = (Boolean[]) arr;
            boolean[] res = new boolean[N];
            System.arraycopy(tmp, 0, res, 0, N);
            return res;
        }

        return null;
    }

    public static int[] flatten(int[][] arr) {
        int N = 0;
        for (int i = 0; i < arr.length; ++i) N += arr[i].length;
        int[] res = new int[N];
        int idx = 0;
        for (int i = 0; i < arr.length; ++i)
            for (int j = 0; j < arr[i].length; ++j) res[idx++] = arr[i][j];
        return res;
    }

    public static int[] randomIndex(int from, int to) {
        int[] arr = new int[to - from];
        for (int i = from; i < to; ++i) arr[i - from] = i;
        RandomUtils.shuffle(arr);
        return arr;
    }


    public static void printlnConcisely(Object arr1) {
        printlnConcisely(arr1, null, 100);
    }

    public static void printlnConcisely(Iterable arr1) {
        printlnConcisely(arr1, null, 100);
    }

    public static void printlnConcisely(Object arr1, String spliter, PrintWriter out, int howManyOfOneRow) {
        if (out == null) {
            out = new PrintWriter(System.out);
        }
        Object[] arr = inbox(arr1);
        for (int i = 0; i < arr.length; ++i) {
            if (i > 0 && i % howManyOfOneRow == 0) out.println();
            if (i % howManyOfOneRow != 0) out.print(spliter);
            out.print(arr[i]);
        }
        out.println();
        out.flush();
    }

    public static void printlnConcisely(Object arr1, PrintWriter out, int howManyOfOneRow) {
        printlnConcisely(arr1, " ", out, howManyOfOneRow);
    }

    public static void printlnConcisely(Iterable arr1, String spliter, PrintWriter out, int howManyOfOneRow) {
        if (out == null) {
            out = new PrintWriter(System.out);
        }
        Iterator iterator = arr1.iterator();
        int i = 0;
        while (true) {
            if (!iterator.hasNext()) break;
            if (i > 0 && i % howManyOfOneRow == 0) out.println();
            if (i % howManyOfOneRow != 0) out.print(spliter);
            out.print(iterator.next());
            i++;
        }
        out.println();
        out.flush();
    }

    public static void printlnConcisely(Iterable arr1, PrintWriter out, int howManyOfOneRow) {
        printlnConcisely(arr1, " ", out, howManyOfOneRow);
    }

    public static void reverse(int[] arr) {
        reverse(arr, 0, arr.length);
    }

    public static void reverse(int[] arr, int from, int to) {
        if (from < 0 || to > arr.length || from >= to) throw new RuntimeException();
        int l = from, r = to - 1;
        while (true) {
            if (l >= r) break;
            int t = arr[l];
            arr[l] = arr[r];
            arr[r] = t;
            l++; r--;
        }
    }

    public static int upperBound(int[] arr, int value) {
        return upperBound(arr, 0, arr.length, value);
    }

    public static int upperBound(int[] arr, int from, int to, int value) {
        if (from < 0 || to > arr.length || from >= to) throw new RuntimeException();
        int l = from, r = to;
        while (true) {
            if (l >= r) break;
            int mid = l + (r - l) / 2;
            if (arr[mid] <= value) l = mid + 1;
            else r = mid;
        }
        if (l != r) throw new RuntimeException();
        return l;
    }

    public static <T> int upperBound(List<? extends Comparable<? super T>> arr, T value) {
        return upperBound(arr, 0, arr.size(), value);
    }

    public static <T> int upperBound(List<? extends Comparable<? super T>> arr, int from, int to, T value) {
        if (from < 0 || to > arr.size() || from >= to) throw new RuntimeException();
        int l = from, r = to;
        while (true) {
            if (l >= r) break;
            int mid = l + (r - l) / 2;
            if (arr.get(mid).compareTo(value) <= 0) l = mid + 1;
            else r = mid;
        }
        if (l != r) throw new RuntimeException();
        return l;
    }

    public static int lowerBound(int[] arr, int value) {
        return lowerBound(arr, 0, arr.length, value);
    }

    public static int lowerBound(int[] arr, int from, int to, int value) {
        if (from < 0 || to > arr.length || from >= to) throw new RuntimeException();
        int l = from, r = to;
        while (true) {
            if (l >= r) break;
            int mid = l + (r - l) / 2;
            if (arr[mid] >= value) r = mid;
            else l = mid + 1;
        }
        if (l != r) throw new RuntimeException();
        return l;
    }

    /**
     *
     * @param arr MUST be SORTED.
     * @return
     */
    public static int[] unique(int[] arr) {
        int[] res = new int[arr.length];
        int p, q;
        p = q = 0;
        while (q < arr.length) {
            if (p == 0 || arr[q] != res[p - 1]) res[p++] = arr[q];
            q++;
        }
        return ArrayUtils.subArray(res, 0, p);
    }

    public static int[] subArray(int[] original, int from, int to) {
        int[] res = new int[to - from];
        for (int i = from; i < to; ++i) res[i - from] = original[i];
        return res;
    }

    public static int minIndex(double[] array) {
        int index = 0;
        for (int i = 1; i < array.length; ++i)
            if (array[i] < array[index]) index = i;
        return index;
    }

    public static <T extends Comparable<T>> Tuple3<Integer, Integer, T> minIndex(T[][] arr) {
        if (arr == null || arr.length == 0) throw new IllegalArgumentException();
        int I, J; T K;
        I = J = -1; K = null;
        for (int i = 0; i < arr.length; ++i)
            for (int j = 0; j < arr[i].length; ++j) if (K == null || K.compareTo(arr[i][j]) > 0) {
                K = arr[i][j];
                I = i; J = j;
            }
        return new Tuple3<Integer, Integer, T>(I, J, K);
    }


    public static int compare(int[] as, int[] bs) {
        int i, j;
        for (i = j = 0; i < as.length && j < bs.length; ++j, ++i) {
            if (as[i] == bs[j]) continue;
            return as[i] - bs[j];
        }
        if (i == as.length && j == bs.length) return 0;
        return i < as.length ? 1 : -1;
    }

    public static <T> List<T> asList(T[] xs) {
        List<T> res = new ArrayList<T>();
        for (T x : xs) res.add(x);
        return res;
    }
}
