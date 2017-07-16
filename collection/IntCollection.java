package template.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author dy[jealousing@gmail.com] on 17-3-28.
 */
public interface IntCollection {

    int size();

    boolean isEmpty();

    boolean contains(int o);

    PrimitiveIterator.OfInt iterator();

    int[] toArray();

    Integer[] toArray(Integer[] a);

    boolean add(int integer);

    boolean removeOne(int o);

    boolean containsAll(IntCollection c);

    boolean addAll(IntCollection c);

    boolean removeAll(IntCollection c);

    boolean removeIf(Predicate<? super Integer> filter);

    boolean retainAll(Collection<?> c);

    void clear();

    boolean equals(IntCollection o);

    int hashCode();

    Spliterator.OfInt spliterator();

    IntStream stream();

    IntStream parallelStream();

}
