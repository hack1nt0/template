package template.egork.generated.collections.set;

import template.egork.generated.collections.IntCollection;

public interface IntSet extends IntCollection {
    @Override
    default public int count(int value) {
        return contains(value) ? 1 : 0;
    }
}
