package template.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * @author dy[jealousing@gmail.com] on 17-4-4.
 */
public class TaskScheduler {
    static ForkJoinPool forkJoinPool = new ForkJoinPool();

    public static ForkJoinTask<Void> task(Runnable supplier) {
        return schedule(supplier);
    }

    public static <T> ForkJoinTask<T> task(Supplier<T> supplier) {
        return schedule(supplier);
    }

    public static void parallel(Runnable... suppliers) {
        int n = suppliers.length;
        ForkJoinTask<Void>[] tasks = new ForkJoinTask[n - 1];
        for (int i = 0; i < n - 1; ++i) tasks[i] = task(suppliers[i]);
        suppliers[n - 1].run();
        for (int i = 0; i < n - 1; ++i) tasks[i].join();
    }

    public static <T> List<T> parallel(Supplier<T>... suppliers) {
        int n = suppliers.length;
        ForkJoinTask<T>[] tasks = new ForkJoinTask[n - 1];
        for (int i = 0; i < n - 1; ++i) tasks[i] = task(suppliers[i]);
        List<T> res = new ArrayList<T>(n);
        res.set(n - 1, suppliers[n - 1].get());
        for (int i = 0; i < n - 1; ++i) res.set(i, tasks[i].join());
        return res;
    }


    private static <T> ForkJoinTask<T> schedule(Supplier<T> supplier) {
        RecursiveTask<T> task = new RecursiveTask<T>() {
            @Override
            protected T compute() {
                return supplier.get();
            }
        };
        if (Thread.currentThread() instanceof ForkJoinWorkerThread) task.fork();
        else forkJoinPool.execute(task);
        return task;
    }

    private static ForkJoinTask<Void> schedule(Runnable function) {
        RecursiveAction task = new RecursiveAction() {
            /**
             * The main computation performed by this task.
             */
            @Override
            protected void compute() {
                function.run();
            }
        };
        if (Thread.currentThread() instanceof ForkJoinWorkerThread) task.fork();
        else forkJoinPool.execute(task);
        return task;
    }
}
