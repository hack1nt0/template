package template.misc;

import java.util.concurrent.ForkJoinPool;

/**
 * @author dy[jealousing@gmail.com] on 17-3-21.
 */
@FunctionalInterface
public interface IntComparator {
    public int compare(int a, int b);
}
