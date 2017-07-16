package template.collection;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * @author dy[jealousing@gmail.com] on 17-3-28.
 */
public interface IntList extends IntCollection {

    int get(int index);

    int set(int index, int element);

    void add(int index, int element);

    int remove(int index);
}
