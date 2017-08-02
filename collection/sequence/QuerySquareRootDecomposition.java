package template.collection.sequence;

import template.collection.intervals.Interval;
import template.debug.Stopwatch;
import template.numbers.IntUtils;
import template.string.StringUtils;

import java.util.*;

/**
 * Created by dy on 17-1-19.
 *
 * Also known as Mo's Algorithm. It is an offline algorithm, which means the querys must be told ahead.
 *
 * This Algo has complexity of   O(sqrt(capacity)*max(m,capacity)), m means the capacity.o. of queries, and capacity means the length of queried arr.
 * When m <= capacity, the compexity is O(sqrt(capacity)*capacity).
 *
 * While not better than IntevalTree, which complexity is O(mlgn), it's complexity is stable.
 * For Example, querying the number of every occurrence elements of from given range, the IntevalTree will
 * maintain from cntMap for each node, so the cost of every query may degenerate to O(wlgn), w means the radical of queried arr. But with QSRT-decom-
 * position, the amortized cost of every query is O(sqrt(capacity)) because of its offline thing.
 *
 * The key points of QSRT-decomp is Sorting and Amortized Analysis of complexity.
 */
public class QuerySquareRootDecomposition {
    private int[] intArray;
    private int N;
    private int bucketSize;

    public QuerySquareRootDecomposition(int[] array) {
        assert array.length > 0;
        this.intArray = array;
        this.N = array.length;
        this.bucketSize = (int)Math.sqrt(N);
    }

    public int[] queryOffline(Interval[] queries) {
        if (queries.length > intArray.length) throw new RuntimeException("This algo maybe not approciated.");

        //Integer[] index = new Integer[queries.length];
        Integer[] index = (Integer[]) ArrayUtils.inbox(ArrayUtils.index(queries.length));
        Arrays.sort(index, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                int bucket1 = queries[o1].left() / bucketSize;
                int bucket2 = queries[o2].left() / bucketSize;
                if (bucket1 != bucket2) return bucket1 - bucket2;
                return queries[o1].right() - queries[o2].right();
            }
        });

        return solve(queries, index);
    }

    private int[] solve(Interval[] queries, Integer[] index) {
        /**
         * Task Specified
         */
        int[] uniques = new int[queries.length];
        int cnt = 0;
        HashMap<Integer, Integer> cntMap = new HashMap<>();
        /**
         * end
         */
        int preL = 0, preR = 0;
        for (int i = 0; i < queries.length; ++i) {
            int curL = queries[index[i]].left();
            int curR = queries[index[i]].right();
            // Add first!
            while (true) {
                if (preR >= curR && curL >= preL) break;
                if (preR < curR) {
                    plus(preR, cntMap);
                    preR++;
                }
                if (curL < preL) {
                    preL--;
                    plus(preL, cntMap);
                }
            }
            //Sub then!
            while (true) {
                if (preL >= curL && curR >= preR) break;
                if (preL < curL) {
                    minus(preL, cntMap);
                    preL++;
                }
                if (curR < preR) {
                    preR--;
                    minus(preR, cntMap);
                }
            }
            if (!(preL == curL && curR == preR)) throw new RuntimeException();
//            Set<Integer> set = new HashSet<>();
//            for (int j = curL; j < curR; ++j) set.add(intArray[j]);
//            if (!set.equals(cntMap.keySet())) {
//                ArrayUtils.printlnTableH(set.toArray(new Integer[0]), cntMap.keySet().toArray(new Integer[0]));
//            }
            /**
             * Task Specified
             */
            uniques[index[i]] = cntMap.size();
        }
        return uniques;
    }

    /**
     * Task Specified
     */
    private void plus(int i, Map<Integer, Integer> cntMap) {
        //if (i < 0 || i >= intArray.length) return;
        //throw new NotImplementedException();
        if (!cntMap.containsKey(intArray[i])) {
            cntMap.put(intArray[i], 1);
        } else {
            cntMap.put(intArray[i], cntMap.get(intArray[i]) + 1);
        }
    }

    /**
     * Task Specified
     */
    private void minus(int i, Map<Integer, Integer> cntMap) {
        //if (i < 0 || i >= intArray.length) return;
        //throw new NotImplementedException();
        int c = cntMap.get(intArray[i]);
        if (c == 1) cntMap.remove(intArray[i]);
        else cntMap.put(intArray[i], c - 1);
    }

    public int[] queryBruteforce(Interval[] queries) {
        Integer[] index = (Integer[]) ArrayUtils.inbox(ArrayUtils.index(queries.length));
        return solve(queries, index);
    }


    public static void main(String[] args) {
        while (true) test1();
    }

    private static void test1() {
        int N = 100000, M = 100000;
        int[] arr = IntUtils.randomInts(N, 0, 10);
        QuerySquareRootDecomposition querySquareRootDecomposition = new QuerySquareRootDecomposition(arr);
        Interval[] interval1Ds = new Interval[M];
        Random random = new Random();
        for (int i = 0; i < M; ++i) {
            int l = random.nextInt(N + 1);
            int r = random.nextInt(N + 1);
            interval1Ds[i] = new Interval(Math.min(l, r), Math.max(l, r));
        }
        Stopwatch.tic();
        int[] ans1 = querySquareRootDecomposition.queryOffline(interval1Ds);
        Stopwatch.toc();
        Stopwatch.tic();
        int[] ans2 = querySquareRootDecomposition.queryBruteforce(interval1Ds);
        Stopwatch.toc();
        System.out.println(StringUtils.repeat("-", 20));
        if (!Arrays.equals(ans1, ans2)) {
            throw new RuntimeException();
            //ArrayUtils.println(ans1, ans2);
        }
    }
}
