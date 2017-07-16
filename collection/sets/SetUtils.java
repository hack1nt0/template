package template.collection.sets;

import java.util.ArrayList;
import java.util.Set;

/**
 * @author dy[jealousing@gmail.com] on 17-1-22.
 */
public class SetUtils {

    public static <T> int intersect(Set<T> a, Set<T> b) {
        return intersect(a, b, null);
    }

    public static <T> int intersect(Set<T> a, Set<T> b, Set<T> res) {
        if (a.size() > b.size()) {
            Set<T> t = a;
            a = b;
            b = t;
        }
        int n = 0;
        for (T e : a) {
            if (b.contains(e)) {
                n++;
                if (res != null) res.add(e);
            }
        }
        return n;
    }

    public static <T> int union(Set<T> a, Set<T> b) {
        return union(a, b, null);
    }

    public static <T> int union(Set<T> a, Set<T> b, Set<T> res) {
        if (res == null) {
            return a.size() + b.size() - intersect(a, b);
        }
        for (T e : a) res.add(e);
        for (T e : b) res.add(e);
        return res.size();
    }

    public static <T> int subtract(Set<T> a, Set<T> b) {
        return subtract(a, b, null);
    }

    public static <T> int subtract(Set<T> a, Set<T> b, Set<T> res) {
        if (res == null) {
            return a.size() - intersect(a, b);
        }
        for (T e : a) {
            if (!b.contains(e)) res.add(e);
        }
        return res.size();
    }
}
