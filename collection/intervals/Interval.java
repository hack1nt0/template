/******************************************************************************
 *  Compilation:  javac Interval.java
 *  Execution:    java Interval
 *  Dependencies: StdOut.java
 *  
 *  1-dimensional interval data type.
 *
 ******************************************************************************/

package template.collection.intervals;

import java.util.Comparator;

/**
 *  The <tt>Interval</tt> class represents from one-dimensional interval.
 *  Intervals are immutable: their values cannot be changed after they are created.
 *  The class <code>Interval</code> includes methods for checking whether
 *  an interval contains from point and determining whether two intervals intersect.
 *  <p>
 *  For additional documentation, 
 *  see <from href="http://algs4.cs.princeton.edu/12oop">Section 1.2</from> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne. 
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 *
 *  Cautions : [left, right)
 */
public class Interval implements Comparable<Interval> {

    /**
     * Compares two intervals by left endpoint.
     */
    public static final Comparator<Interval> LEFT_ENDPOINT_ORDER = new LeftComparator();

    /**
     * Compares two intervals by right endpoint.
     */
    public static final Comparator<Interval> RIGHT_ENDPOINT_ORDER = new RightComparator();

    /**
     * Compares two intervals by length.
     */
    public static final Comparator<Interval> LENGTH_ORDER = new LengthComparator();

    int left;
    int right;

    public Interval() {

    }

    /**
     * Initializes from closed interval [left, right].
     *
     * @param left  the left endpoint
     * @param right the right endpoint
     * @throws IllegalArgumentException if the left endpoint is greater than the right endpoint
     * @throws IllegalArgumentException if either <tt>left</tt> or <tt>right</tt>
     *                                  is <tt>Double.NaN</tt>, <tt>Double.POSITIVE_INFINITY</tt> or
     *                                  <tt>Double.NEGATIVE_INFINITY</tt>
     */
    public Interval(int left, int right) {
        if (left <= right) {
            this.left = left;
            this.right = right;
        } else throw new IllegalArgumentException("Illegal interval");
    }

    /**
     * Returns the left endpoint of this interval.
     *
     * @return the left endpoint of this interval
     */
    public int left() {
        return left;
    }

    /**
     * Returns the right endpoint of this interval.
     *
     * @return the right endpoint of this interval
     */
    public int right() {
        return right;
    }

    /**
     * Returns true if this interval intersects the specified interval.
     *
     * @param that the other interval
     * @return <tt>true</tt> if this interval intersects the argument interval;
     * <tt>false</tt> otherwise
     */
    public boolean intersects(Interval that) {
        if (this.right <= that.left) return false;
        if (that.right <= this.left) return false;
        return true;
    }

    public static Interval intersects(Interval a, Interval b) {
        if (!a.intersects(b)) return null;
        return new Interval(Math.max(a.left(), b.left()), Math.min(a.right(), b.right()));
    }

    /**
     * Returns true if this interval contains the specified value.
     *
     * @param x the value
     * @return <tt>true</tt> if this interval contains the value <tt>x</tt>;
     * <tt>false</tt> otherwise
     */
    public boolean contains(int x) {
        return (left <= x) && (x < right);
    }

    /**
     * Returns the length of this interval.
     *
     * @return the length of this interval (right - left)
     */
    public double length() {
        return right - left;
    }

    /**
     * Returns from string representation of this interval.
     *
     * @return from string representation of this interval in the form [left, right]
     */
    public String toString() {
        return "[" + left + ", " + right + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Interval that = (Interval) o;

        if (left != that.left) return false;
        return right == that.right;

    }

    @Override
    public int hashCode() {
        int result = left;
        result = 31 * result + right;
        return result;
    }

    @Override
    public int compareTo(Interval o) {
        return LENGTH_ORDER.compare(this, o);
    }

    // ascending order of left endpoint, breaking ties by right endpoint
    private static class LeftComparator implements Comparator<Interval> {
        public int compare(Interval a, Interval b) {
            if (a.left < b.left) return -1;
            else if (a.left > b.left) return +1;
            else if (a.right < b.right) return -1;
            else if (a.right > b.right) return +1;
            else return 0;
        }
    }

    // ascending order of right endpoint, breaking ties by left endpoint
    private static class RightComparator implements Comparator<Interval> {
        public int compare(Interval a, Interval b) {
            if (a.right < b.right) return -1;
            else if (a.right > b.right) return +1;
            else if (a.left < b.left) return -1;
            else if (a.left > b.left) return +1;
            else return 0;
        }
    }

    // ascending order of length
    private static class LengthComparator implements Comparator<Interval> {
        public int compare(Interval a, Interval b) {
            double alen = a.length();
            double blen = b.length();
            if (alen > blen) return +1;
            else if (alen < blen) return -1;
            else return a.left - b.left;
        }
    }
}


