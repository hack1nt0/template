package template.string;

import template.collection.tuple.Tuple2;

import java.util.*;

/**
 * Created by dy on 17-1-13.
 *
 * todo involve Knuth's optim.
 */
public class KMP {
    private int[] back;
    private String pattern;

    public KMP(String pattern) {
        if (pattern == null || pattern.length() == 0) throw new IllegalArgumentException();
        this.pattern = pattern;
        int n = pattern.length();
        back = new int[n + 1];
        back[0] = 0;
        if (n == 1) return;
        back[1] = 0;
        for (int i = 2; i <= n; ++i) {
            int b = back[i - 1];
            while (b > 0 && pattern.charAt(i - 1) != pattern.charAt(b)) b = back[b];
            if (pattern.charAt(i - 1) == pattern.charAt(b))
                back[i] = b + 1;
            else
                back[i] = 0;
        }
    }

    public List<Tuple2<Integer, Integer>> exploit(String text) {
        List<Tuple2<Integer, Integer>> res = new ArrayList<>();
        for (int pt = 0, pp = 0; pt < text.length();) {
            while (pp > 0 && text.charAt(pt) != pattern.charAt(pp)) pp = back[pp];
            if (text.charAt(pt) == pattern.charAt(pp)) {
                pp++;
                pt++;
                if (pp == pattern.length()) {
                    res.add(new Tuple2<>(pt - pattern.length(), pt));
                    pp = back[pattern.length()];
                }
            }
            else pt++;
        }

        return res;
    }

    public Iterator<Tuple2<Integer, Integer>> exploitLazily(String text) {
        return new Iterator<Tuple2<Integer, Integer>>() {
            private Tuple2<Integer, Integer> res;
            private int pp, pt;
            @Override
            public boolean hasNext() {
                for (;pt < text.length(); ++pt) {
                    while (true) {
                        if (pp == 0 || text.charAt(pt) == pattern.charAt(pp)) break;
                        pp = back[pp];
                    }
                    if (text.charAt(pt) == pattern.charAt(pp))
                        pp++;
                    else if (pp == 0)
                        continue;

                    if (pp == pattern.length()) {
                        res = new Tuple2<>(pt + 1 - pattern.length(), pt + 1);
                        pp = back[pattern.length()];
                        return true;
                    }
                }
                return true;
            }

            @Override
            public Tuple2<Integer, Integer> next() {
                return res;
            }
        };
    }

    public int[] getBack() {
        return back;
    }

    public static void main(String[] args) {
        test1();
    }

    public static void test1() {
        while (true) {
            String pattern = StringUtils.random(2, 'a', 'c' + 1);
            String text = StringUtils.random(10, 'a', 'c' + 1);
            List<Tuple2<Integer, Integer>> ans1 = StringUtils.search(text, pattern);
            List<Tuple2<Integer, Integer>> ans2 = new KMP(pattern).exploit(text);

            Collections.sort(ans1, Tuple2.FIRST_ELEMENT_ORDER);
            Collections.sort(ans2, Tuple2.FIRST_ELEMENT_ORDER);
            if (!equals(ans1, ans2)) {
                throw new RuntimeException();
            }
        }
    }

    private static <T extends Comparable<T>> boolean equals(List<T> a, List<T> b) {
        if (a.size() != b.size()) return false;
        for (int i = 0; i < a.size(); ++i) if (a.get(i).compareTo(b.get(i)) != 0) return false;
        return true;
    }

}
