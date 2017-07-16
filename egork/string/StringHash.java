package egork.string;

/**
 * @author Egor Kulikov (egor@net.egork.net)
 */
public interface StringHash {
    long hash(int from, int to);

    long hash(int from);

    int length();
}
