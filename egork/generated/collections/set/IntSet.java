package egork.generated.collections.set;

import egork.generated.collections.IntCollection;

public interface IntSet extends IntCollection {
    @Override
    default public int count(int value) {
        return contains(value) ? 1 : 0;
    }
}
