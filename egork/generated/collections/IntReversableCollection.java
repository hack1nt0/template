package egork.generated.collections;

import egork.generated.collections.iterator.IntIterator;

public interface IntReversableCollection extends IntCollection {
    //abstract
    public IntIterator reverseIterator();

    //base
    default public int last() {
        return reverseIterator().value();
    }

    default IntStream reversed() {
        return () -> reverseIterator();
    }
}
