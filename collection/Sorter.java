package template.collection;

import template.concurrency.TaskScheduler;
import template.debug.RandomUtils;
import template.debug.Stopwatch;
import template.misc.IntComparator;
import template.numbers.IntUtils;

import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;

/**
 * @author dy[jealousing@gmail.com] on 17-3-20.
 */
public class Sorter {
    private static final int CUTOFF        =  15;   // cutoff for insertion sort
    private static final int R             = Character.MAX_VALUE + 1;   // extended ASCII alphabet capacity

    /* Sort */
    public <T extends Comparable<T>> List<T> sort(Iterable<T> iterable, Comparator<T> comparator) {
        List<T> list = CollectionUtils.asList(iterable);
        Collections.sort(list, comparator);
        return list;
    };

    public void sort(CharSequence[] array) {
        sort(Arrays.asList(array));
    }

    public void sort(List<CharSequence> stringList) {
        sortMSDWay3(stringList, 0, stringList.size(), 0);
    };

    /**
     * Way-3 partition of MSD
     * Complexity: O(nm), capacity means num of sorted strs, m means max capacity of sorted strs.
     * while so, it only occur in the situation of many strs are of equal ones(or at least common long-same-prefix).
     * Its amortized complexity is O(nlg_3^capacity)
     * More, it can be optimized by insertion sort.
     * In practice, sortMSDWay3 will be faster than sortMSD, because of dynamic space allocation of count array of sortMSD.
     * And also, it is faster than Arrays.sort for most cases.
     *
     * Space: O(m), (the stack space), m means max capacity of sorted strs.
     * @param stringList strings to sort
     */
    private static void sortMSDWay3(List<CharSequence> stringList, int lo, int hi, int d) {
        // cutoff isString insertion sort for small subarrays
        if (hi <= lo + 15) {
            insertion(stringList, lo, hi - 1, d);
            return;
        }
        int cd = charAt(stringList.get(lo), d);
        way3Partition(stringList, lo, hi, d);
        int p = lo;
        while (true) {
            if (p < hi && charAt(stringList.get(p), d) < cd) p++;
            else break;
        }
        sortMSDWay3(stringList, lo, p, d);
        int q = p;
        while (true) {
            if (q < hi && charAt(stringList.get(q), d) == cd) q++;
            else break;
        }
        sortMSDWay3(stringList, p, q, d + 1);
        sortMSDWay3(stringList, q, hi, d);
    }

    /**
     * The loop variant:
     for (int i = lo; i < p1; ++i) assert charAt(ss[i], d) < cd;
     for (int i = p1; i < p2; ++i) assert charAt(ss[i], d) == cd;
     for (int i = p3 + 1; i < hi; ++i) assert charAt(ss[i], d) > cd;
     **/
    private static void way3Partition(List<CharSequence> stringList, int lo, int hi, int d) {
        int cd = charAt(stringList.get(lo), d);
        int p1 = lo, p2 = lo, p3 = hi - 1;
        while (true) {
            if (p2 > p3) break;
            if (charAt(stringList.get(p2), d) < cd) swap(stringList, p2++, p1++);
            else if (charAt(stringList.get(p2), d) == cd) p2++;
            else if (charAt(stringList.get(p2), d) > cd) swap(stringList, p2, p3--);
        }
    }

    // return dth character of s, -1 if d = length of string
    private static int charAt(CharSequence s, int d) {
        if (d >= s.length()) return -1;
        return s.charAt(d);
    }

    // insertion sort from[lo..hi], starting at dth character
    private static void insertion(List<CharSequence> stringList, int lo, int hi, int d) {
        for (int i = lo; i <= hi; i++)
            for (int j = i; j > lo && less(stringList.get(j), stringList.get(j-1), d); j--)
                swap(stringList, j, j-1);
    }

    // is v less than w, starting at character d
    private static boolean less(CharSequence v, CharSequence w, int d) {
        // assert v.substring(0, d).equals(w.substring(0, d));
        for (int i = d; i < Math.min(v.length(), w.length()); i++) {
            if (v.charAt(i) < w.charAt(i)) return true;
            if (v.charAt(i) > w.charAt(i)) return false;
        }
        return v.length() < w.length();
    }

    // exchange from[i] and from[j]
    private static void swap(List<CharSequence> stringList, int i, int j) {
        CharSequence swap = stringList.get(i);
        stringList.set(i, stringList.get(j));
        stringList.set(j, swap);
    }
    /* End of String */


    /* Primary Array */

    public static void sort(int[] array, IntComparator comparator) {
        if (comparator == null) {
            comparator = new IntComparator() {
                @Override
                public int compare(int a, int b) {
                    return a - b;
                }
            };
        }
        mergeSort(array, comparator);
    }
    /**
     * @complexity O(nlogn) and stable
     * In practice, it is slower than Arrays.sort by 6~7x.
     * @param array
     */
    public static void mergeSort(int[] array, IntComparator comparator) {
        int N = array.length;
        if (N < 2) return;
        int[] tmp = new int[N];
        for (int len = 1; len < N; len *= 2) {
            for (int from = 0; from + len < N; from += 2 * len) {
                int p = from + len;
                int q = p + len;
                if (q > N) q = N;
                if (q - from < 7) {
                    insertionSort(array, from, q, comparator);
                    continue;
                }
                int i = from;
                int j = p;
                int k = from;
                while (true) {
                    if (j == q && i == p) break;
                    // <=(not <) here to maintain original order of elements
                    if (j == q || i < p && comparator.compare(array[i], array[j]) <= 0) {
                        tmp[k++] = array[i++];
                    } else {
                        tmp[k++] = array[j++];
                    }
                }
                //assert i == p && j == q && k == q;
                if (comparator.compare(array[p], array[p - 1]) < 0) {
                    //faster than loop
                    System.arraycopy(tmp, from, array, from, q - from);
                }
            }
        }
    }

    public static void parallelSort(int[] array) {
        int threshold = 100000;
        int[] aux = new int[array.length];
        parallelSortHelper(array, 0, array.length, threshold, aux);
    }

    private static void parallelSortHelper(int[] array, int from, int to, int threshold, int[] aux) {
        if (to - from <= threshold) Arrays.sort(array, from, to);
        else {
            int mid = from + (to - from) / 2;
            TaskScheduler.parallel(() -> parallelSortHelper(array, from, mid, threshold, aux),
                    () -> parallelSortHelper(array, mid, to, threshold, aux));
            int i = from, j = mid, k = from;
            while (i < mid || j < to) {
                if (i < mid && j < to && array[i] < array[j] || j == to) aux[k++] = array[i++];
                else aux[k++] = array[j++];
            }
            System.arraycopy(aux, from, array, from, to - from);
        }
    }


    /**
     * @complexity O(capacity^2) and stable
     * @param arr
     * @param from
     * @param to
     */
    public static void insertionSort(int[] arr, int from, int to, IntComparator comparator) {
        for (int i = from + 1; i < to; ++i) {
            for (int j = from; j < i; ++j) {
                if (comparator.compare(arr[i], arr[j]) < 0) {
                    int arri = arr[i];
                    for (int k = i; k > j; --k) arr[k] = arr[k - 1];
                    arr[j] = arri;
                    break;
                }
            }
        }
    }

    /**
     * sorting an array of extended ASCII strings or integers using LSD radix sort.
     *  @author Robert Sedgewick
     *  @author Kevin Wayne
     * Least Significant Digit(LSD).
     * Complexity: O(nm), capacity means num of sorted strs, m means max capacity of sorted strs.
     * Space: O(capacity + w), w means radical of char of sorted strs.
     * @param ss strings to sort
     */
    public static void sortLSD(String[] ss) {
        int W = 0;
        for (String s : ss) {
            W = Math.max(W, s.length());
        }
        int N = ss.length;
        String[] aux = new String[N];

        for (int d = W-1; d >= 0; d--) {
            // sort by key-indexed counting on dth character

            // compute frequency counts
            int[] count = new int[R + 2];
            //Arrays.fill(count, 0);

            for (int i = 0; i < N; i++)
                count[charAt(ss[i], d) + 2]++;

            // compute cumulates
            for (int r = 0; r < R; r++)
                count[r+1] += count[r];

            // move data
            for (int i = 0; i < N; i++)
                aux[count[charAt(ss[i], d) + 1]++] = ss[i];

            // copy back
            for (int i = 0; i < N; i++)
                ss[i] = aux[i];
        }

    }

    /**
     * sorting an array of extended ASCII strings or integers using MSD radix sort.
     *  @author Robert Sedgewick
     *  @author Kevin Wayne
     * Most Significant Digit(MSD).
     * Complexity: O(nm), capacity means num of sorted strs, m means max capacity of sorted strs.
     * while so, it only occur in the situation of many strs are of equal ones(or at least common long-same-prefix).
     * Its amortized complexity is O(nlg_w^capacity), w means radical of char of sorted strs.
     * More, it can be optimized by insertion sort.
     * In practice, sortMSD will be much faster than sortLSD, but slower than Arrays.sort
     *
     * Space: O(capacity + w), w means radical of char of sorted strs.
     * @param ss strings to sort
     */
    public static void sortMSD(List<CharSequence> ss) {
        int N = ss.size();
        sortMSD(ss, 0, N-1, 0, new CharSequence[N]);
    }

    /**
     * sort from from[lo] isString from[hi], starting at the dth character
     */
    private static void sortMSD(List<CharSequence> stringList, int lo, int hi, int d, CharSequence[] aux) {

        // cutoff isString insertion sort for small subarrays
        if (hi <= lo + CUTOFF) {
            insertion(stringList, lo, hi, d);
            return;
        }

        // compute frequency counts
        int[] count = new int[R+2];
        for (int i = lo; i <= hi; i++) {
            int c = charAt(stringList.get(i), d);
            count[c+2]++;
        }

        // transform counts isString indicies
        for (int r = 0; r < R+1; r++)
            count[r+1] += count[r];

        // distribute
        for (int i = lo; i <= hi; i++) {
            int c = charAt(stringList.get(i), d);
            aux[count[c+1]++] = stringList.get(i);
        }

        // copy back
        for (int i = lo; i <= hi; i++)
            stringList.set(i, aux[i - lo]);


        // recursively sort for each character (excludes sentinel -1)
        for (int r = 0; r < R; r++)
            sortMSD(stringList, lo + count[r], lo + count[r+1] - 1, d+1, aux);
    }

    public static void countingSort(int[] array, int W) {
        countingSort(array, IntUnaryOperator.identity(), W);
    }

    public static <T> void countingSort(int[] index, IntUnaryOperator mapper, int W) {
        countingSort(index, mapper, W, null);
    }

    public static void countingSort(int[] index, IntUnaryOperator mapper, int W, int[] aux) {
        if (index == null || index.length == 0) throw new IllegalArgumentException();
        if (aux == null) aux = new int[index.length];
        if (aux.length != index.length) throw new IllegalArgumentException();
        int min = Integer.MAX_VALUE;
        for (int i : index) min = Math.min(min, mapper.applyAsInt(i));
        int offset = 0;
        if (min < 0) offset = -min;
        int[] count = new int[W + 1];
        for (int i : index) count[mapper.applyAsInt(i) + offset + 1]++;
        for (int i = 0; i < W; ++i) count[i + 1] += count[i];
        for (int i = 0; i < aux.length; ++i) aux[count[mapper.applyAsInt(index[i]) + offset]++] = index[i];
        for (int i = 0; i < aux.length; ++i) index[i] = aux[i];
    }
    /* End of Sort */

    public static int reverseOrderPairs(int[] arr) {
        int[] buf = new int[arr.length];
        return reverseOrderPairs(arr, 0, arr.length, buf);
    }

    public static int reverseOrderPairs(int[] arr, int from, int to, int[] buf) {
        if (from + 1 >= to) return 0;
        int mid = from + (to - from) / 2;
        int res = reverseOrderPairs(arr, from, mid, buf) + reverseOrderPairs(arr, mid, to, buf);
        int i = from, j = mid, k = from;
        while (true) {
            if (i == mid && j == to) break;
            if (j == to || i < mid && arr[i] <= arr[j]) buf[k++] = arr[i++];
            else {
                res += mid - i;
                buf[k++] = arr[j++];
            }
        }
        System.arraycopy(buf, from, arr, from, to - from);
        return res;
    }

    public static boolean sorted(int[] arr) {
        return sorted(arr, 0, arr.length);
    }

    public static boolean sorted(int[] arr, int from, int to) {
        assert from <= to : "[" + from + ", " + to + ")";
        int prev = Integer.MIN_VALUE;
        for (int i = from; i < to; ++i)
            if (i > from && arr[i - 1] > arr[i]) return false;
        return true;
    }

    public static void main(String[] args) {
        //testSort();
        //testReverseOrderPairs();
        System.out.println(ForkJoinPool.getCommonPoolParallelism());
        testParallelSort();
    }

    private static void testParallelSort() {
        while (true) {
            int[] arr = IntUtils.randomInts(100000000, -10, 10);
            int[] arr1 = arr.clone();
            int[] arr2 = arr.clone();
            Stopwatch.tic();
            //mergeSort(arr1, (int a, int b) -> a - b);
            //Arrays.parallelSort(arr1);
            Arrays.sort(arr1);
            Stopwatch.toc();
            Stopwatch.tic();
            parallelSort(arr2);
            Stopwatch.toc();
            System.out.println();
            if (!Arrays.equals(arr1, arr2)) {
                System.out.println(Arrays.toString(arr1));
                System.out.println(Arrays.toString(arr2));
                System.out.println("error");
            }
        }
    }

    private static void testSort() {
        while (true) {
//            int[] arr = IntUtils.randomInts(10000000, -100000000, 100000000);
//            int[] arr1 = arr.clone();
//            int[] arr2 = arr.clone();
//            Stopwatch.tic();
//            mergeSort(arr1);
//            Stopwatch.toc();
//            Stopwatch.tic();
//            Arrays.sort(arr2);
//            Stopwatch.toc();
//            if (!Arrays.equals(arr1, arr2)) {
//                //ArrayUtils.printlnTableH(arr1, arr2);
//                System.out.println("error");
//            }
            int W = 100000;
            int[] arr = IntUtils.randomInts(100000, 0, W);
            countingSort(arr, W);
            if (!sorted(arr)) {
                throw new RuntimeException();
            }
        }
    }

//    public static void testSort() {
//        int N = 20000, W = 10000;
//        Random rand = new Random();
//        String[] ss = new String[N];
//        int t = 0;
//
//        while (true) {
//            for (int i = 0; i < N; ++i)
//                ss[i] = random(W, 'a', 'z' + 1);
//            //ss[i] = random(W);
//
//            String[] ans1 = ss.clone();
//            String[] ans2 = ss.clone();
//            String[] ans3 = ss.clone();
//            String[] ans4 = ss.clone();
//            tic();
//            sortMSDWay3(ans1);
//            toc();
//            tic();
//            sortMSD(ans2);
//            toc();
//            tic();
//            Arrays.sort(ans3);
//            toc();
////            tic();
////            sortLSD(ans4);
////            toc();
//            System.out.println(StringUtils.repeat("-", 20));
//
//            if (!Arrays.deepEquals(ans1, ans2) || !Arrays.deepEquals(ans1, ans3)) {
//                throw new RuntimeException();
//            }
//        }
//    }

    private static void testReverseOrderPairs() {
        while (true) {
            int[] arr = IntUtils.randomInts(RandomUtils.uniform(100), 0, 10);
            int ans = 0;
            for (int i = 0; i < arr.length; ++i)
                for (int j = i + 1; j < arr.length; ++j) if (arr[i] > arr[j]) ans++;
            if (ans != reverseOrderPairs(arr)) {
                throw new RuntimeException();
            }
        }
    }
}
