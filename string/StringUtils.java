package template.string;

import template.collection.tuple.Tuple2;
import template.debug.RandomUtils;

import java.util.*;

import static template.debug.Stopwatch.*;


/**
 * Created by dy on 17-1-11.
 *
 * the sorted
 */
public class StringUtils {

    public static String reverse(String s) {
        StringBuilder res = new StringBuilder(s);
        return res.reverse().toString();
    }

    public static String random(int W, int from, int to) {
        assert from < to;
        StringBuilder res = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < W; ++i)
            res.append((char)(from + random.nextInt(to - from)));
        return res.toString();
    }

    public static String repeat(String str, int n) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < n; ++i) res.append(str);
        return res.toString();
    }

    public static List<Tuple2<Integer, Integer>> search(String text, String pattern) {
        List<Tuple2<Integer, Integer>> res = new ArrayList<>();
        for (int i = 0; i + pattern.length() <= text.length();) {
            int p = text.indexOf(pattern, i);
            if (p == -1) {
                i++;
            } else {
                res.add(new Tuple2<>(p, p + pattern.length()));
                i = p + 1;
            }
        }
        return res;
    }

    /**
     * Invariable
     *         (1) s[i] < s[i+1] <...< s[j-1] (?) s[j]
     *         (2) i is the answer
     *
     * NOTE lots of implementation on the internet are wrong.
     * @param origin
     * @return Cyclic Minimum Representation of String.
     */
    public static int cyclicMin(CharSequence origin) {
        if (origin == null || origin.length() == 0) throw new IllegalArgumentException();
        if (origin.length() == 1) return 0;
        int n = origin.length();
//        StringBuilder stringBuilder = new StringBuilder(origin);
//        stringBuilder.append(origin);
        int i = 0, j = 1;
        while (i < n && j < n) {
            if (origin.charAt(i) < origin.charAt(j)) {j++; continue;}
            if (origin.charAt(i) > origin.charAt(j)) {
                i = j;
                j++;
                continue;
            }
            int offset = 0;
            while (offset < n) {
                int ii = i + offset;
                if (ii >= n) ii -= n;
                int jj = j + offset;
                if (jj >= n) jj -= n;
                char ichar = origin.charAt(ii);
                char jchar = origin.charAt(jj);
                if (ichar < jchar) {
                    j += offset + 1;
                    break;
                }
                if (ichar > jchar) {
                    i = j;
                    j += Math.min(j - i, offset);
                    if (j == i) j++;
                    break;
                }
                offset++;
            }
            if (offset == n) {
                break;
            }
        }
        if (i >= n) throw new RuntimeException();
        return i;
    }

    /**
     * 'ABCABCAB' -> 'ABC'
     * @param origin
     * @return Minimum Repeating Substring of origin.
     */
    public static int minRepeatedSubstring(String origin) {
        int[] back = new KMP(origin).getBack();
        return origin.length() - back[origin.length()];
    }

    /**
     * BKDR Hash Function (https://www.byvoid.com/zhs/blog/string-hash-compare)
     */
    @SuppressWarnings("overflow")
    public static int hash(String string, int from, int to) {
        int hash = 0;
        int seed = 131; // 31 131 1313 13131 131313 etc..
        for (int i = from; i < to; ++i) hash = hash * seed + string.charAt(i);
        hash &= 0x7FFFFFFF;
        return hash;
    }

    public static int hash(String string) { return hash(string, 0, string.length());}

    public static <T extends Comparable<T>> int compare(Iterable<T> a, Iterable<T> b) {
        Iterator<T> ita = a.iterator();
        Iterator<T> itb = b.iterator();
        while (ita.hasNext() && itb.hasNext()) {
            int cmp = ita.next().compareTo(itb.next());
            if (cmp != 0) return cmp;
        }
        if (!ita.hasNext() && !itb.hasNext()) return 0;
        return ita.hasNext() ? +1 : -1;
    }

    /** Unit Tests **/
    public static void main(String[] args) {
        testCyclicMin();
    }

    private static void testCyclicMin() {
        while (true) {
            String s = random(10, 'a', 'b' + 1);
            StringBuilder sb = new StringBuilder(s);
            sb.append(s);
            int from = cyclicMin(s);
            cyclicMin("bbabb");
            String min = sb.substring(from, from + s.length());
            for (int i = 0; i < s.length(); ++i) {
                String t = sb.substring(i, i + s.length());
                if (t.compareTo(min) < 0) {
                    throw new RuntimeException();
                }
            }
        }
    }

    private static void testMinRepeated() {
        while (true) {
            String s = random(10, 'a', 'b' + 1);
            int to = minRepeatedSubstring(s);
            System.out.println(s + '\t' + s.substring(0, to));
            for (int i = to; i < s.length(); ++i) {
                if (s.charAt(i % to) != s.charAt(i)) throw new RuntimeException();
            }
        }
    }

}
