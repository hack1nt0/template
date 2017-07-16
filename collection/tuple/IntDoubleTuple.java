package template.collection.tuple;

import template.geometry.GeometryUtils;

import java.util.Comparator;

/**
 * Created by dy on 17-1-15.
 */
public class IntDoubleTuple implements Comparable<IntDoubleTuple> {
    int first;
    double second;


    public static final Comparator<IntDoubleTuple> FIRST_FIRST_ORDER = new FirstComparator();
    public static final Comparator<IntDoubleTuple> SENCOND_FIRST_ORDER = new SecondComparator();


    public IntDoubleTuple(int first, double second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public int compareTo(IntDoubleTuple o) {
        return FIRST_FIRST_ORDER.compare(this, o);
    }

    private static class FirstComparator implements Comparator<IntDoubleTuple> {
        public int compare(IntDoubleTuple a, IntDoubleTuple b) {
            int firstcmp = a.first - b.first;
            if (firstcmp != 0) return firstcmp;
            double diff = a.second - b.second;
            if (Math.abs(diff) < GeometryUtils.epsilon) return 0;
            return diff > 0 ? +1 : -1;
        }
    }

    private static class SecondComparator implements Comparator<IntDoubleTuple> {
        public int compare(IntDoubleTuple a, IntDoubleTuple b) {
            double diff = a.second - b.second;
            if (Math.abs(diff) < GeometryUtils.epsilon) {
                return a.first - b.first;
            }
            else return diff > 0 ? +1 : -1;
        }
    }

    @Override
    public String toString() {
        return first + " " + second;
    }

    public int getFirst() {
        return first;
    }

    public double getSecond() {
        return second;
    }
}
