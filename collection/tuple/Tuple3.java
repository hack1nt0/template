package template.collection.tuple;

import template.collection.sequence.ArrayUtils;

import java.util.Comparator;

/**
 * Created by dy on 17-1-15.
 */
public class Tuple3<T extends Comparable, U extends Comparable, V extends Comparable> {
    T first;
    U second;
    V third;

    public static final Comparator<Tuple3> FIRST_TO_LAST_ORDER = new FirstComparator();
    public static final Comparator<Tuple3> LAST_TO_FIRST_ORDER = new SecondComparator();


    public Tuple3(T first, U second, V third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }
    private static class FirstComparator implements Comparator<Tuple3> {
        public int compare(Tuple3 a, Tuple3 b) {
            int firstcmp = a.first.compareTo(b.first);
            if (firstcmp != 0) return firstcmp;
            int secondcmp = a.second.compareTo(b.second);
            if (secondcmp != 0) return secondcmp;
            int thirdcmp = a.third.compareTo(b.third);
            if (thirdcmp != 0) return thirdcmp;
            return 0;
        }
    }

    private static class SecondComparator implements Comparator<Tuple3> {
        public int compare(Tuple3 a, Tuple3 b) {
            int thirdcmp = a.third.compareTo(b.third);
            if (thirdcmp != 0) return thirdcmp;
            int secondcmp = a.second.compareTo(b.second);
            if (secondcmp != 0) return secondcmp;
            int firstcmp = a.first.compareTo(b.first);
            if (firstcmp != 0) return firstcmp;
            return 0;
        }
    }

    public T getFirst() {
        return first;
    }

    public U getSecond() {
        return second;
    }

    public V getThird() {
        return third;
    }


    @Override
    public String toString() {
        return first + " " + second + " " + third;
    }
}
