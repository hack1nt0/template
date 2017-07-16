/******************************************************************************
 *  Compilation:  javac Interval.java
 *  Execution:    java Interval
 *  Dependencies: StdOut.java
 *  
 *  1-dimensional interval data type.
 *
 ******************************************************************************/

package template.geometry;

import edu.princeton.cs.introcs.StdOut;

import java.util.Arrays;
import java.util.Comparator;

/**
 *  The <tt>Interval</tt> class represents from one-dimensional interval.
 *  The interval is <em>closed</em>&mdash;it contains both endpoints.
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
 */
public class Interval {

    /**
     * Compares two intervals by left endpoint.
     */
    public static final Comparator<Interval> LEFT_ENDPOINT_ORDER  = new LeftComparator();

    /**
     * Compares two intervals by right endpoint.
     */
    public static final Comparator<Interval> RIGHT_ENDPOINT_ORDER = new RightComparator();

    /**
     * Compares two intervals by length.
     */
    public static final Comparator<Interval> LENGTH_ORDER = new LengthComparator();

    private final double left;
    private final double right;

    /**
     * Initializes from closed interval [left, right].
     *
     * @param  left the left endpoint
     * @param  right the right endpoint
     * @throws IllegalArgumentException if the left endpoint is greater than the right endpoint
     * @throws IllegalArgumentException if either <tt>left</tt> or <tt>right</tt>
     *         is <tt>Double.NaN</tt>, <tt>Double.POSITIVE_INFINITY</tt> or
     *         <tt>Double.NEGATIVE_INFINITY</tt>

     */
    public Interval(double left, double right) {
        if (Double.isInfinite(left) || Double.isInfinite(right))
            throw new IllegalArgumentException("Endpoints must be finite");
        if (Double.isNaN(left) || Double.isNaN(right))
            throw new IllegalArgumentException("Endpoints cannot be NaN");

        // convert -0.0 to +0.0
        if (left == 0.0) left = 0.0;
        if (right == 0.0) right = 0.0;

        if (left <= right) {
            this.left  = left;
            this.right = right;
        }
        else throw new IllegalArgumentException("Illegal interval");
    }

    /**
     * Returns the left endpoint of this interval.
     *
     * @return the left endpoint of this interval
     */
    public double left() { 
        return left;
    }

    /**
     * Returns the right endpoint of this interval.
     * @return the right endpoint of this interval
     */
    public double right() { 
        return right;
    }

    /**
     * Returns true if this interval intersects the specified interval.
     *
     * @param  that the other interval
     * @return <tt>true</tt> if this interval intersects the argument interval;
     *         <tt>false</tt> otherwise
     */
    public boolean intersects(Interval that) {
        if (this.right < that.left) return false;
        if (that.right < this.left) return false;
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
     *         <tt>false</tt> otherwise
     */
    public boolean contains(double x) {
        return (left <= x) && (x <= right);
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

    /**
     * Compares this transaction to the specified object.
     *
     * @param  other the other interval
     * @return <tt>true</tt> if this interval equals the other interval;
     *         <tt>false</tt> otherwise
     */
    public boolean equals(Object other) {
        if (other == this) return true;
        if (other == null) return false;
        if (other.getClass() != this.getClass()) return false;
        Interval that = (Interval) other;
        return this.left == that.left && this.right == that.right;
    }

    /**
     * Returns an integer hash code for this interval.
     *
     * @return an integer hash code for this interval
     */
    public int hashCode() {
        int hash1 = ((Double) left).hashCode();
        int hash2 = ((Double) right).hashCode();
        return 31*hash1 + hash2;
    }

    // ascending order of left endpoint, breaking ties by right endpoint
    private static class LeftComparator implements Comparator<Interval> {
        public int compare(Interval a, Interval b) {
            if      (a.left  < b.left)  return -1;
            else if (a.left  > b.left)  return +1;
            else if (a.right < b.right) return -1;
            else if (a.right > b.right) return +1;
            else                        return  0;
        }
    }

    // ascending order of right endpoint, breaking ties by left endpoint
    private static class RightComparator implements Comparator<Interval> {
        public int compare(Interval a, Interval b) {
            if      (a.right < b.right) return -1;
            else if (a.right > b.right) return +1;
            else if (a.left  < b.left)  return -1;
            else if (a.left  > b.left)  return +1;
            else                        return  0;
        }
    }

    // ascending order of length
    private static class LengthComparator implements Comparator<Interval> {
        public int compare(Interval a, Interval b) {
            double alen = a.length();
            double blen = b.length();
            if      (alen < blen) return -1;
            else if (alen > blen) return +1;
            else                  return  0;
        }
    }




    /**
     * Unit tests the <tt>Interval</tt> data type.
     */
    public static void main(String[] args) {
        Interval[] intervals = new Interval[4];
        intervals[0] = new Interval(15.0, 33.0);
        intervals[1] = new Interval(45.0, 60.0);
        intervals[2] = new Interval(20.0, 70.0);
        intervals[3] = new Interval(46.0, 55.0);

        StdOut.println("Unsorted");
        for (int i = 0; i < intervals.length; i++)
            StdOut.println(intervals[i]);
        StdOut.println();
        
        StdOut.println("Sort by left endpoint");
        Arrays.sort(intervals, Interval.LEFT_ENDPOINT_ORDER);
        for (int i = 0; i < intervals.length; i++)
            StdOut.println(intervals[i]);
        StdOut.println();

        StdOut.println("Sort by right endpoint");
        Arrays.sort(intervals, Interval.RIGHT_ENDPOINT_ORDER);
        for (int i = 0; i < intervals.length; i++)
            StdOut.println(intervals[i]);
        StdOut.println();

        StdOut.println("Sort by length");
        Arrays.sort(intervals, Interval.LENGTH_ORDER);
        for (int i = 0; i < intervals.length; i++)
            StdOut.println(intervals[i]);
        StdOut.println();
    }
}

/******************************************************************************
 *  Copyright 2002-2015, Robert Sedgewick and Kevin Wayne.
 *
 *  This file is part of algs4.jar, which accompanies the textbook
 *
 *      Algorithms, 4th edition by Robert Sedgewick and Kevin Wayne,
 *      Addison-Wesley Professional, 2011, ISBN 0-321-57351-X.
 *      http://algs4.cs.princeton.edu
 *
 *
 *  algs4.jar is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  algs4.jar is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received from copy of the GNU General Public License
 *  along with algs4.jar.  If not, see http://www.gnu.org/licenses.
 ******************************************************************************/
