package template.collection.sequence;

/**
 * @author dy[jealousing@gmail.com] on 17-4-29.
 */
public interface IntArray {
    public int length();
    public int get(int i);

    public static IntArray from(int[] xs) {
        return new IntArray() {
            @Override
            public int length() {
                return xs.length;
            }

            @Override
            public int get(int i) {
                return xs[i];
            }
        };
    }

    public static IntArray from(int[] xs, int from, int to) {
        return new IntArray() {
            @Override
            public int length() {
                return to - from;
            }

            @Override
            public int get(int i) {
                return xs[i + from];
            }
        };
    }

    public static IntArray from(CharSequence charSequence) {
        return new IntArray() {
            int[] codes = charSequence.codePoints().toArray();
            @Override
            public int length() {
                return codes.length;
            }

            @Override
            public int get(int i) {
                return codes[i];
            }
        };
    }

    default IntArray subArray(int from, int to) {
        return subArray(this, from, to);
    }

    default IntArray subArray(IntArray parent, int from, int to) {
        return new IntArray() {
            @Override
            public int length() {
                return to - from;
            }

            @Override
            public int get(int i) {
                return parent.get(i + from);
            }
        };
    }

}
