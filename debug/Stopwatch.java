package template.debug;

/**
 * Created by dy on 17-1-11.
 */
public class Stopwatch {
    private static long t1 = System.currentTimeMillis();

    public static void toc() {
        long t2 = System.currentTimeMillis();
        System.out.printf("%.3fs\n", (t2 - t1) / 1000.);
    }

    public static void tic() {
        t1 = System.currentTimeMillis();
    }
}
