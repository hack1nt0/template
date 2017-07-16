package egork.generated.collections.queue;

import egork.generated.collections.CharCollection;

public interface CharQueue extends CharCollection {
    default public char first() {
        return peek();
    }

    public char peek();

    public char poll();
}
