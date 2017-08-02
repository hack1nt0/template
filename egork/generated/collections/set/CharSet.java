package template.egork.generated.collections.set;

import template.egork.generated.collections.CharCollection;

public interface CharSet extends CharCollection {
    @Override
    default public int count(char value) {
        return contains(value) ? 1 : 0;
    }
}
