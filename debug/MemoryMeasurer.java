package template.debug;

import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.vm.VM;
import template.collection.sequence.ArrayUtils;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created by dy on 17-1-20.
 */
public class MemoryMeasurer {

    public static void main(String[] args) {
        testJOL();
    }

    public static void testJOL() {
        PrintWriter pw = new PrintWriter(System.out, true);
        pw.println(VM.current().details());


        Set<Integer> hashset = new HashSet<Integer>();
        Random rng = new Random();
        int n = 10000;
        for (int i = 1; i <= n; i++) {
            hashset.add(rng.nextInt());
        }
        Integer[] arr = new Integer[n];
        for (int i = 0; i < n; i++) {
            arr[i] = i;
        }
        int[] arr2 = (int[]) ArrayUtils.outbox(arr);

        String last = null;
        for (int c = 0; c < 100; c++) {
            String current = ClassLayout.parseClass( arr2.getClass()).toPrintable(arr2);

            if (last == null || !last.equalsIgnoreCase(current)) {
                pw.println(current);
                last = current;
            }

            System.gc();
        }

        pw.close();
    }
}
