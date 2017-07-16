package template.string;

import template.collection.sequence.IntArray;
import template.numbers.IntUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Modified by dy on 17-1-13.
 */
public class IntTrieSet extends TrieSet {

    public IntTrieSet(int radix) {
        super(radix);
    }

    public boolean add(int[] xs) { return add(IntArray.from(xs, 0, xs.length)); }

    public boolean contains(int[] xs, int from, int to) { return contains(IntArray.from(xs, from, to)); }

    public boolean contains(int[] xs) { return contains(xs, 0, xs.length); }

    public boolean contains(int d) { return contains(IntUtils.toArray(d)); }

    public boolean containsPrefix(int[] xs, int from, int to) { return containsPrefix(IntArray.from(xs, from, to)); }

    public Iterator<String> iterator() {
        List<String> ans = new ArrayList<>();
        toList(root, new StringBuilder(), ans);
        return ans.iterator();
    }

    protected void toList(Node cur, StringBuilder prefix, List<String> ans) {
        if (cur.ending) ans.add(prefix.toString());
        for (int i = 0; i < R; ++i) if (cur.next[i] != null) {
            prefix.append(i);
            toList(cur.next[i], prefix, ans);
            prefix.deleteCharAt(prefix.length() - 1);
        }
    }

}
