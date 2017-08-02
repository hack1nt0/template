package template.concurrency;

import java.io.PrintWriter;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author egor@net.egork.net
 */
public class Scheduler {
    private final AtomicInteger testsRemaining;
    private final AtomicInteger threadsRemaining;

    public Scheduler(Scanner in, PrintWriter out, TaskFactory factory, int numParallel) {
        try {
            int ctests = in.nextInt();
            testsRemaining = new AtomicInteger(ctests);
            threadsRemaining = new AtomicInteger(numParallel);
            Task[] tasks = new Task[testsRemaining.get()];
            for (int i = 0; i < tasks.length; i++) {
                tasks[i] = factory.newTask();
            }
            for (Task task : tasks) {
                task.read(in);
                new Thread(() -> {
                    boolean freeThread = false;
                    synchronized (this) {
                        do {
                            try {
                                wait(10);
                            } catch (InterruptedException ignored) {
                            }
                            if (threadsRemaining.get() != 0) {
                                synchronized (threadsRemaining) {
                                    if (threadsRemaining.get() != 0) {
                                        threadsRemaining.decrementAndGet();
                                        freeThread = true;
                                    }
                                }
                            }
                        } while (!freeThread);
                    }
                    task.solve();
                    System.err.print("Jobs left: " + testsRemaining.decrementAndGet() + "/" + ctests + "\r");
                    threadsRemaining.incrementAndGet();
                }).start();
            }
            synchronized (this) {
                while (testsRemaining.get() > 0) {
                    wait(10);
                }
            }
            for (int i = 0; i < tasks.length; i++) {
                tasks[i].write(out, i + 1);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
