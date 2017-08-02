package template.egork.generated.collections.queue;

import template.egork.generated.collections.LongCollection;

public interface LongQueue extends LongCollection {
    default public long first() {
        return peek();
    }

    public long peek();

    public long poll();
}
