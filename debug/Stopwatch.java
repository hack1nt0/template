package template.debug;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Stack;

/**
 * Created by dy on 17-1-11.
 */
public class Stopwatch {
    private static long t1 = System.currentTimeMillis();
    private Deque<Long> stack = new ArrayDeque<>();
    static boolean TURN_ON = true;

    public static void toc() {
        if (TURN_ON) {
            long t2 = System.currentTimeMillis();
            System.err.printf("%.3fs\n", (t2 - t1) / 1000.);
        }
    }

    public static void tic() {
        if (TURN_ON) {
            t1 = System.currentTimeMillis();
        }
    }

    public void start() {
        if (TURN_ON) {
            stack.push(System.currentTimeMillis());
        }
    }

    public void stop() {
        if (TURN_ON) {
            long span = System.currentTimeMillis() - stack.pop();
            System.err.println(span / 1000.0 + "s");
        }
    }
}
