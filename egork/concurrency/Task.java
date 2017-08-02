package template.egork.concurrency;

import template.egork.utils.io.OutputWriter;
import template.egork.utils.io.InputReader;

/**
 * @author egor@net.egork.net
 */
public interface Task {
    public void read(InputReader in);

    public void solve();

    public void write(OutputWriter out, int testNumber);
}
