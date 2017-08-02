package template.egork.generated.collections.queue;

import template.egork.generated.collections.IntCollection;

public interface IntQueue extends IntCollection {
    default public int first() {
        return peek();
    }

    public int peek();

    public int poll();
}
