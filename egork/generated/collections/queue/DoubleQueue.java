package egork.generated.collections.queue;

import egork.generated.collections.DoubleCollection;

public interface DoubleQueue extends DoubleCollection {
    default public double first() {
        return peek();
    }

    public double peek();

    public double poll();
}
