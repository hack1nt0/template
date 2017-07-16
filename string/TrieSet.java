package template.string;

import template.collection.sequence.IntArray;

import java.util.*;

/**
 * Modified by dy on 17-1-13.
 */
public class TrieSet implements Iterable<String> {
    protected int R;
    protected int N;
    protected class Node {
        Node[] next;
        boolean ending;
        public Node(int radix) {
            next = new Node[radix];
        }
    }
    protected Node root;

    public TrieSet(int radix) {
        this.R = radix;
        this.root = new Node(radix);
    }

    public boolean add(IntArray intArray) {
        boolean res = false;
        Node cur = root;
        for (int i = 0; i < intArray.length(); ++i) {
            int c = intArray.get(i);
            if (cur.next[c] == null) {
                cur.next[c] = new Node(R);
                res = true;
                cur = cur.next[c];
            } else {
                cur = cur.next[c];
            }
        }
        cur.ending = true;
        N++;
        return res;
    }

    public boolean add(CharSequence charSequence) { return add(IntArray.from(charSequence)); }

    public boolean contains(IntArray intArray) {
        Node cur = root;
        for (int i = 0; i < intArray.length(); ++i) {
            int c = intArray.get(i);
            if (cur.next[c] == null) return false;
            cur = cur.next[c];
        }
        return cur.ending;
    }

    public boolean contains(CharSequence charSequence) { return contains(IntArray.from(charSequence)); }

    public boolean containsPrefix(IntArray intArray) {
        Node cur = root;
        for (int i = 0; i < intArray.length(); ++i) {
            Node next = cur.next[intArray.get(i)];
            if (next == null) return false;
            cur = next;
        }
        return true;
    }

    public Iterator<String> iterator() {
        List<String> ans = new ArrayList<>();
        toList(root, new StringBuilder(), ans);
        return ans.iterator();
    }

    protected void toList(Node cur, StringBuilder prefix, List<String> ans) {
        if (cur.ending) ans.add(prefix.toString());
        for (char i = 0; i < R; ++i) if (cur.next[i] != null) {
            prefix.append(i);
            toList(cur.next[i], prefix, ans);
            prefix.deleteCharAt(prefix.length() - 1);
        }
    }

    public void remove(IntArray key) {
        root = remove(root, key, 0);
    }

    private Node remove(Node x, IntArray key, int d) {
        if (x == null) return null;
        if (d == key.length()) {
            N--;
            x.ending = false;
        } else {
            int c = key.get(d);
            x.next[c] = remove(x.next[c], key, d+1);
        }
        if (x.ending) return x;
        for (int c = 0; c < R; c++) if (x.next[c] != null) return x;
        return null;
    }

    public void remove(CharSequence charSequence) {
        remove(IntArray.from(charSequence));
    }

    public void setR(int R) { this.R = R; }

    public int getR() { return this.R; }

    public int size() { return N; }
}
