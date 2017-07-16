package template.string;

import template.collection.Sorter;
import template.collection.sequence.ImmutableIntList;
import template.misc.IntComparator;

import java.util.*;

/**
 * Created by dy on 17-1-12.
 *
 * rank[index[i]] == i
 */
public class SuffixArray {

    private ImmutableIntList text;
    private int n;
    private int[] index;
    private int[] rank;
    private int[] height;

    public SuffixArray(CharSequence text) {
        this.text = new ImmutableIntList() {
            @Override
            public int size() {
                return text.length();
            }

            @Override
            public int get(int index) {
                return text.charAt(index);
            }
        };
        buildSA(this.text);
    }

    public SuffixArray(int[] array) {
        this.text = new ImmutableIntList() {
            @Override
            public int size() {
                return array.length;
            }

            @Override
            public int get(int index) {
                return array[index];
            }
        };
        buildSA(this.text);
    }

    public void buildSA(ImmutableIntList text) {
        if (text == null || text.size() == 0) throw new IllegalArgumentException();
        this.n = text.size();
        this.index = new int[n];
        for (int i = 0; i < n; ++i) index[i] = i;
        Sorter.sort(index, new IntComparator() {
            @Override
            public int compare(int a, int b) {
                return text.get(a) - text.get(b);
            }
        });
        this.rank = new int[n];
        for (int i = 0; i < n; ++i) {
            if (i > 0 && text.get(index[i]) == text.get(index[i - 1])) rank[index[i]] = rank[index[i - 1]];
            else rank[index[i]] = i;
        }

        int[] tmp = new int[n];
        PairComparator comparator = new PairComparator(rank);
        for (int len = 1; len < n; len *= 2) {
            comparator.len = len;
            Sorter.sort(index, comparator);
            tmp[index[0]] = 0;
            for (int i = 1; i < n; ++i) {
                int cmp = comparator.compare(index[i], index[i - 1]);
                assert cmp >= 0;
                if (cmp == 0) tmp[index[i]] = tmp[index[i - 1]];
                else tmp[index[i]] = i;
            }
            for (int i = 0; i < n; ++i) rank[i] = tmp[i];
            //small optimization
            boolean unique = true;
            for (int i = 1; i < n; ++i) if (rank[index[i]] == rank[index[i - 1]]) {
                unique = false;
                break;
            }
            if (unique) break;
        }
    }

    private class PairComparator implements IntComparator {
        int len;
        int[] index;
        public PairComparator(int[] index) {
            this.index = index;
        }
        @Override
        public int compare(int o1, int o2) {
            if (index[o1] != index[o2]) return index[o1] - index[o2];
            int a = o1 + len < index.length ? index[o1 + len] : -1;
            int b = o2 + len < index.length ? index[o2 + len] : -1;
            return a - b;
        }
    }

    public int[] getHeight() {
        if (height == null) {
            height = new int[n];
            int h = 0;
            for (int i = 0; i < n; ++i) {
                if (h > 0) h--;
                if (rank[i] == 0) continue;
                int j = index[rank[i] - 1];
                while (i + h < n && j + h < n && text.get(i + h) == text.get(j + h)) h++;
                height[rank[i]] = h;
            }
        }
        return height;
    }

    public int index(int i) {
        if (i < 0 || i >= n) throw new IllegalArgumentException();
        return index[i];
    }

    public int[] getIndex() {
        return index;
    }

    public int rank(int i) {
        if (i < 0 || i >= n) throw new IllegalArgumentException();
        return index[i];
    }

    public int[] getRank() {
        return rank;
    }

    public int[] indexBruteForce() {
        int[] index = new int[n];
        for (int i = 0; i < n; ++i) index[i] = i;
        Sorter.sort(index, new IntComparator() {
            @Override
            public int compare(int o1, int o2) {
                int offset = 0;
                while(true) {
                    if (offset < Math.min(n - o1, n - o2)) {
                        if (text.get(o1 + offset) != text.get(o2 + offset))
                            return text.get(o1 + offset) - text.get(o2 + offset);
                        offset++;
                        continue;
                    }
                    if (offset >= n - o1 && offset >= n - o2) {
                        throw new RuntimeException();
                    }
                    if (offset >= n - o1) {
                        return -1;
                    }
                    if (offset >= n - o2) {
                        return 1;
                    }
                }
            }
        });

        return index;
    }

    public int[] heightBruteForce() {
        int[] height = new int[n];
        for (int i = 0; i < n; ++i) {
            if (rank[i] == 0) continue;
            int j = index[rank[i] - 1];
            int h = 0;
            while (i + h < n && j + h < n && text.get(i + h) == text.get(j + h)) h++;
            height[rank[i]] = h;
        }
        return height;
    }


    public static void main(String[] args) {
        while (true) {
            String s = StringUtils.random(400, 'a', 'z' + 1);
            SuffixArray sa = new SuffixArray(s);
            int[] index1 = sa.getIndex();
            int[] index2 = sa.indexBruteForce();
            if (!Arrays.equals(index1, index2)) {
                throw new RuntimeException();
            }
            int[] h1 = sa.getHeight();
            int[] h2 = sa.heightBruteForce();
            String format = "%4s\t%-10s\t%6s\n";
            if (!Arrays.equals(h1, h2)) {
                System.out.printf(format, "from", "suffix", "height");
                for (int i = 0; i < s.length(); ++i) {
                    System.out.printf(format, sa.index(i), s.substring(sa.index(i)), h1[i]);
                }
                throw new RuntimeException();
            }
        }
    }
}
