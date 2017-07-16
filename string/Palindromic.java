package template.string;

import template.collection.sequence.ImmutableIntList;
import template.collection.tuple.Tuple2;
import template.debug.RandomUtils;

/**
 * @author dy[jealousing@gmail.com] on 17-3-27.
 */
public class Palindromic {
    public static Tuple2<Integer, Integer> manacher(ImmutableIntList string) {
        if (string == null || string.size() == 0) throw new IllegalArgumentException();
        int n = string.size() * 2 + 1;
        int maxRightCenter = 0;
        int longestCenter = 0;
        int[] lp = new int[n];
        for (int center = 1; center < n; ++center) {
            int maxRight = maxRightCenter + lp[maxRightCenter] / 2;
            int offset = center <= maxRight ? Math.min(maxRight - center + 1, lp[maxRightCenter * 2 - center] / 2 + 1) : 1;
            while (true) {
                int i = center - offset;
                int j = center + offset;
                if (0 <= i && j < n && equals(i, j, string)) {offset++; continue;}
                break;
            }
            lp[center] = (offset - 1) * 2;
            if (center % 2 != 0) lp[center]++;
            if (lp[center] > lp[longestCenter]) longestCenter = center;
            if (center + lp[center] / 2 > maxRight) maxRightCenter = center;
        }

        int from = (longestCenter - lp[longestCenter] / 2) / 2;
        int to = (longestCenter + lp[longestCenter] / 2 - 1) / 2 + 1;
        return new Tuple2<>(from, to);
    }

    private static boolean equals(int i, int j, ImmutableIntList string) {
        if (i % 2 == 0) return true;
        return string.get(i / 2) == string.get(j / 2);
    }

    public static boolean is(CharSequence s) {
        int l = 0, r = s.length() - 1;
        while (true) {
            if (l >= r) break;
            if (s.charAt(l++) != s.charAt(r--)) return false;
        }
        return true;
    }

    public static String random(int n, int from, int to) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < (n + 1) / 2; ++i) res.append((char) RandomUtils.uniform(from, to));
        for (int i = res.length() - (n % 2 == 0 ? 1 : 2); i >= 0; --i)
            res.append(res.charAt(i));
        return res.toString();
    }

    public static void main(String[] args) {
        String string = "AA";
        System.out.println(manacher(new ImmutableIntList() {
            @Override
            public int size() {
                return string.length();
            }

            @Override
            public int get(int index) {
                return string.charAt(index);
            }
        }));
    }

    public static void testRandom() {
        while (true) {
            int n = RandomUtils.uniform(100);
            String pal = random(n, '0', '9' + 1);
            //System.out.printlnConcisely(pal);
            if (pal.length() != n || !is(pal)) {
                System.err.println(pal);
                throw new RuntimeException();
            }
        }
    }
}
