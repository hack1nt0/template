package template.combinatorics;

import template.collection.Sorter;
import template.collection.sequence.ArrayUtils;
import template.debug.RandomUtils;
import template.numbers.IntUtils;
import template.string.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dy on 2017/1/26.
 */
public class CombUtils {

    public static long fact(int n) {
        long res = 1;
        for (int i = 2; i <= n; ++i) res *= i;
        return res;
    }

    public static long choose(int n, int m) {
        return fact(n) / fact(m) / fact(n - m);
    }

    // TODO: 2017/2/5
    public static long modFact(int n, int mod) {
        long res = 1;
        for (int i = 2; i <= n; ++i) res *= i;
        return res;
    }

    // TODO: 2017/2/5
    public static long modChoose(int n, int m, int mod) {
        return fact(n) / fact(m) / fact(n - m);
    }

     // Begin of Permutation

    /**
     * stable when equal objects exist ?
     * @param arr
     * @return
     */
    public static boolean nextPermutation(int[] arr) {
        boolean found = false;
        int n = arr.length;
        for (int i = n - 2; i >= 0; --i) {
            if (arr[i] >= arr[i + 1]) continue;
            int upper = n - 1;
            while (true) {
                if (arr[upper] > arr[i]) break;
                upper--;
            }
            ArrayUtils.swap(arr, i, upper);
            ArrayUtils.reverse(arr, i + 1, n);
            found = true;
            break;
        }
        return found;
    }
    /**
     * @Complexity O(capacity^2)
     * @param arr1
     * @return the permutation to sort the arr. When the arr contains equal objects,
     *         the permutation returned may not be 'optimistic'. That's saying, you
     *         will do more 'swap' operation than needed, when you sort the arr
     *         according the permutation. Furthermore, the sorting is not stable.
     */
    public static int[] permutationForSorting(int[] arr1) {
        int[] arr = arr1.clone();
        int n = arr.length;
        int[] perm = new int[n];
        boolean[] visited = new boolean[n];
        for (int i = 0; i < n; ++i) {
            if (visited[i]) continue;
            int cur = i;
            int curValue = arr[i];
            while (true) {
                visited[cur] = true;
                int next = i;
                for (int j = i + 1; j < n; ++j) if (arr[j] < curValue) next++;
                if (next != i) {
                    //while (next2 < capacity && (arr[next2] == curValue || visited[next2])) next2++; //avoid loop
                    while (next < n && (arr[next] == curValue)) next++; //avoid loop
                    if (next == n) {
                        throw new RuntimeException();
                        //break;
                    }
                }
                //System.err.printlnConcisely(cur + "->" + next);
                int t = curValue;
                curValue = arr[next];
                arr[next] = t;
                perm[cur] = next;
                cur = next;

                if (next == i) break;
            }
        }
        //if (!Sorter.sorted(arr, 0, arr.length)) throw new RuntimeException();
        return perm;
    }

    public static void permute(int[] arr, int[] permutation) {
        int n = arr.length;
        if (n != permutation.length) throw new RuntimeException();
        boolean[] visited = new boolean[n];
        for (int i = 0; i < n; ++i) {
            if (visited[i]) continue;
            int cur = i;
            int curValue = arr[i];
            while (true) {
                visited[cur] = true;
                int next = permutation[cur];
                int t = curValue;
                curValue = arr[next];
                arr[next] = t;
                cur = next;
                if (next == i) break;
            }
        }
    }

    public static List<List<Integer>> rings(int[] permutation) {
        int n = permutation.length;
        boolean[] visited = new boolean[n];
        List<List<Integer>> ans = new ArrayList<>();
        for (int i = 0; i < n; ++i) {
            if (visited[i]) continue;
            List<Integer> ring = new ArrayList<>();
            int cur = i;
            while (true) {
                if (visited[cur]) {
                    throw new RuntimeException();
                }
                ring.add(cur);
                visited[cur] = true;
                cur = permutation[cur];
                if (cur == i) break;
            }
            ans.add(ring);
        }
        return ans;
    }

    // End of Permutation

    public static void main(String[] args) {
        //testPermutation();
        testNextPerm();
    }

    private static void testNextPerm() {
        int[] arr = ArrayUtils.index(4);
        while (true) {
            System.out.println(Arrays.toString(arr));
            if (!nextPermutation(arr)) break;
        }
    }

    private static void testPermutation() {
        while (true) {
            int n = RandomUtils.uniform(100);
            int[] arr = IntUtils.randomInts(n, 0, 10);
            int[] perm = permutationForSorting(arr);
            int[] arr1 = arr.clone();
            permute(arr1, perm);
            if (!Sorter.sorted(arr1)) {
                System.out.println(Arrays.toString(arr));
                System.out.println(Arrays.toString(ArrayUtils.index(n)));
                System.out.println(Arrays.toString(perm));
                System.out.println(rings(perm));
                System.out.println(Arrays.toString(arr1));

                System.out.println(StringUtils.repeat("=", 12));
                throw new RuntimeException();
            }
        }
    }

}
