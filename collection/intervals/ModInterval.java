package template.collection.intervals;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dy on 2017/2/6.
 *
 * Cautions : [left, right), and there is not a ZERO here! -- [a, a) means a fully ModInterval.
 */
public class ModInterval {
    private int left, right, mod;

    public ModInterval(int left, int right, int mod) {
        if (left >= mod || right >= mod) throw new IllegalArgumentException();
        this.left = left;
        this.right = right;
        this.mod = mod;
    }

    public int left() {
        return this.left;
    }

    public int right() {
        return this.right;
    }

    public boolean outOfBound() {
        return right <= left;
    }

    public boolean fully() {
        return length() == mod;
    }

    public boolean intersects(ModInterval that) {
        if (this.mod != that.mod) throw new IllegalArgumentException();
        if (this.fully() || that.fully()) return true;

        if (this.outOfBound() && !that.outOfBound()) {
            return that.left < this.right || this.left < that.right;
        }
        if (!this.outOfBound() && that.outOfBound()) {
            return this.left < that.right || that.left < this.right;
        }
        if (!this.outOfBound() && !that.outOfBound()) {
            return !(this.right <= that.left || that.right <= this.left);
        }
        return true;
    }

    public static List<ModInterval> intersects(ModInterval a, ModInterval b) {
        if (a.mod != b.mod) throw new IllegalArgumentException();
        List<ModInterval> overlap = new ArrayList<>();
        if (!a.intersects(b)) return overlap;
        if (a.length() == a.mod) {
            overlap.add(b);
            return overlap;
        }
        if (b.length() == b.mod) {
            overlap.add(a);
            return overlap;
        }
        if (!a.outOfBound() && b.outOfBound()) {
            ModInterval t = a;
            a = b;
            b = t;
        }
        if (a.outOfBound() && !b.outOfBound()) {
            if (b.left < a.right) overlap.add(new ModInterval(b.left, Math.min(b.right, a.right), a.mod));
            if (a.left < b.right) overlap.add(new ModInterval(Math.max(a.left, b.left), b.right, a.mod));
            return overlap;
        }

        overlap.add(new ModInterval(Math.max(a.left, b.left), Math.min(a.right, b.right), a.mod));
        return overlap;
    }

    public boolean contains(int x) {
        if (left < right) return left <= x && x < right;
        if (left == right) return true;
        if (right < left) return !(right <= x && x < left);

        return false;
    }

    public double length() {
        if (left < right) return right - left;
        if (right <= left) return mod - (left - right);
        return -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ModInterval that = (ModInterval) o;

        if (left != that.left) return false;
        if (right != that.right) return false;
        return mod == that.mod;
    }

    @Override
    public int hashCode() {
        int result = left;
        result = 31 * result + right;
        result = 31 * result + mod;
        return result;
    }

    @Override
    public String toString() {
        return "ModInterval{" +
                "left=" + left +
                ", right=" + right +
                ", mod=" + mod +
                '}';
    }

    public static void main(String[] args) {
        testIntersect();
    }

    public static void testIntersect() {
        ModInterval a = new ModInterval(6, 2, 10);
        ModInterval b = new ModInterval(1, 7, 10);
        for (ModInterval overlap : ModInterval.intersects(a, b))
            System.out.println(overlap);

    }
}
