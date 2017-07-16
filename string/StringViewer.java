package template.string;

/**
 * @author dy[jealousing@gmail.com] on 17-3-20.
 */
public class StringViewer implements Comparable<StringViewer>, CharSequence{
    private String string;
    private int hash;
    private int from, to;


    public StringViewer(String str, int from, int to) {
        this.string = str;
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringViewer that = (StringViewer) o;
        if (string == null) return that.string == null;
        return this.compareTo(that) == 0;
    }
    @Override
    public int hashCode() {
        if (hash == 0) hash = StringUtils.hash(string, from, to);
        return hash;
    }

    @Override
    public int length() {
        return to - from;
    }

    @Override
    public char charAt(int i) {
        return string.charAt(i + from);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return string.substring(from + start, from + end);
    }

    @Override
    public int compareTo(StringViewer o) {
        int i = from, j = o.from;
        while (i < to && j < o.to) {
            char a = string.charAt(i);
            char b = o.string.charAt(j);
            if (a != b) return a - b;
        }
        return length() - o.length();
    }
}
