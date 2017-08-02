package template.concurrency;

import java.io.PrintWriter;
import java.util.Scanner;

/**
 * @author egor@net.egork.net
 */
public interface Task {
    public void read(Scanner in);

    public void solve();

    public void write(PrintWriter out, int testNumber);
}
