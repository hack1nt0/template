package template.debug;

import java.io.*;
import java.math.BigInteger;
import java.util.InputMismatchException;

/**
 * Single thread scanner with only UTF-8 supported.
 *
 * @author Egor Kulikov (kulikov@devexperts.com)
 * @author dy (jealousing@gmail.com)
 *
 * Charactor            Decimal       UTF-8         UTF-16(java char)      UTF-32(code point)
 *  冬(冬的变体字)        194586	    F0 AF A0 9A	     D87E DC1A	              0002F81A
 *
 *
 */
public class ScannerUTF8 {
    private boolean finished = false;
    InputStream stream;
    private byte[] buf = new byte[1024];
    private int curChar;
    private int numChars;
    private SpaceCharFilter filter;

    public ScannerUTF8(InputStream stream) {
        this.stream = stream;
    }

    // read a code point
    public int nextCharacter() {
        int c = read();
        if (c == -1) return -1;
        c &= 0xff;
        int char2, char3, char4;
        int byteCount = 0;
        switch (c >> 4) {
            case 0: case 1: case 2: case 3: case 4: case 5: case 6: case 7:
                    /* 0xxxxxxx*/
                byteCount++;
                return c;
            case 12: case 13:
                    /* 110x xxxx   10xx xxxx*/
                byteCount += 2;
                char2 = read();
                if (char2 == -1)
                    throw new RuntimeException(
                            "malformed input: partial character at end");
                if ((char2 & 0xC0) != 0x80)
                    throw new RuntimeException(
                            "malformed input around byte " + byteCount);
                return (((c & 0x1F) << 6) |
                        (char2 & 0x3F));
            case 14:
                    /* 1110 xxxx  10xx xxxx  10xx xxxx */
                byteCount += 3;
                char2 = read();
                if (char2 == -1)
                    throw new RuntimeException(
                            "malformed input: partial character at end");
                char3 = read();
                if (char3 == -1)
                    throw new RuntimeException(
                            "malformed input: partial character at end");
                if (((char2 & 0xC0) != 0x80) || ((char3 & 0xC0) != 0x80))
                    throw new RuntimeException(
                            "malformed input around byte " + (byteCount - 1));
                return (((c     & 0x0F) << 12) |
                        ((char2 & 0x3F) << 6)  |
                        ((char3 & 0x3F) << 0));
            case 15:
                /* 1111 0xxx 10xx xxxx  10xx xxxx  10xx xxxx */
                if ((c & 0x0F) >> 3 != 0)
                    throw new RuntimeException(
                            "malformed input around byte " + byteCount);
                byteCount += 4;
                char2 = read();
                if (char2 == -1)
                    throw new RuntimeException(
                            "malformed input: partial character at end");
                char3 = read();
                if (char3 == -1)
                    throw new RuntimeException(
                            "malformed input: partial character at end");
                char4 = read();
                if (char4 == -1)
                    throw new RuntimeException(
                            "malformed input: partial character at end");
                if (((char2 & 0xC0) != 0x80) || ((char3 & 0xC0) != 0x80) || ((char4 & 0xC0) != 0x80))
                    throw new RuntimeException(
                            "malformed input around byte " + (byteCount - 1));
                return (((c    & 0x07) << 18 |
                        (char2 & 0x3F) << 12 |
                        (char3 & 0x3F) << 6  |
                        (char4 & 0x3F) << 0));
            default:
                    /* 10xx xxxx,  1111 xxxx */
                throw new RuntimeException(
                        "malformed input around byte " + byteCount);
        }
    }

    public String nextString() {
        StringBuffer chars = new StringBuffer();
        int c = nextCharacter();
        while (isSpaceChar(c)) c = nextCharacter();
        while (!isSpaceChar(c)) {
            chars.appendCodePoint(c);
            c = nextCharacter();
        }
        return chars.toString();
    }

    public int read() {
        if (numChars == -1) {
            throw new InputMismatchException();
        }
        if (curChar >= numChars) {
            curChar = 0;
            try {
                numChars = stream.read(buf);
            } catch (IOException e) {
                throw new InputMismatchException();
            }
            if (numChars <= 0) {
                return -1;
            }
        }
        return buf[curChar++];
    }

    public int peek() {
        if (numChars == -1) {
            return -1;
        }
        if (curChar >= numChars) {
            curChar = 0;
            try {
                numChars = stream.read(buf);
            } catch (IOException e) {
                return -1;
            }
            if (numChars <= 0) {
                return -1;
            }
        }
        return buf[curChar];
    }

    public int nextInt() {
        int c = read();
        while (isSpaceChar(c)) {
            c = read();
        }
        int sgn = 1;
        if (c == '-') {
            sgn = -1;
            c = read();
        }
        int res = 0;
        do {
            if (c < '0' || c > '9') {
                throw new InputMismatchException();
            }
            res *= 10;
            res += c - '0';
            c = read();
        } while (!isSpaceChar(c));
        return res * sgn;
    }

    public long nextLong() {
        int c = read();
        while (isSpaceChar(c)) {
            c = read();
        }
        int sgn = 1;
        if (c == '-') {
            sgn = -1;
            c = read();
        }
        long res = 0;
        do {
            if (c < '0' || c > '9') {
                throw new InputMismatchException();
            }
            res *= 10;
            res += c - '0';
            c = read();
        } while (!isSpaceChar(c));
        return res * sgn;
    }

    public boolean isSpaceChar(int c) {
        if (filter != null) {
            return filter.isSpaceChar(c);
        }
        return isWhitespace(c);
    }

    public static boolean isWhitespace(int c) {
//        return c == ' ' || c == '\capacity' || c == '\r' || c == '\t' || c == -1;
        return 0 <= c && c <= 32;
    }

    private String nextLine0() {
        StringBuilder buf = new StringBuilder();
        int c = nextCharacter();
        while (c != '\n' && c != -1) {
            if (c != '\r') {
                buf.appendCodePoint(c);
            }
            c = nextCharacter();
        }
        return buf.toString();
    }

    public String nextLine() {
        String s = nextLine0();
        while (s.trim().length() == 0) {
            s = nextLine0();
        }
        return s;
    }

    public String nextLine(boolean ignoreEmptyLines) {
        if (ignoreEmptyLines) {
            return nextLine();
        } else {
            return nextLine0();
        }
    }

    public BigInteger nextBigInteger() {
        try {
            return new BigInteger(nextString());
        } catch (NumberFormatException e) {
            throw new InputMismatchException();
        }
    }

    public double nextDouble() {
        int c = read();
        while (isSpaceChar(c)) {
            c = read();
        }
        int sgn = 1;
        if (c == '-') {
            sgn = -1;
            c = read();
        }
        double res = 0;
        while (!isSpaceChar(c) && c != '.') {
            if (c == 'e' || c == 'E') {
                return res * Math.pow(10, nextInt());
            }
            if (c < '0' || c > '9') {
                throw new InputMismatchException();
            }
            res *= 10;
            res += c - '0';
            c = read();
        }
        if (c == '.') {
            c = read();
            double m = 1;
            while (!isSpaceChar(c)) {
                if (c == 'e' || c == 'E') {
                    return res * Math.pow(10, nextInt());
                }
                if (c < '0' || c > '9') {
                    throw new InputMismatchException();
                }
                m /= 10;
                res += (c - '0') * m;
                c = read();
            }
        }
        return res * sgn;
    }

    public boolean isExhausted() {
        int value;
        while (isSpaceChar(value = peek()) && value != -1) {
            read();
        }
        return value == -1;
    }

    public SpaceCharFilter getFilter() {
        return filter;
    }

    public void setFilter(SpaceCharFilter filter) {
        this.filter = filter;
    }

    public interface SpaceCharFilter {
        public boolean isSpaceChar(int ch);
    }

    public void close() {
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ScannerUTF8 fromPath(String path) {
        try {
            return new ScannerUTF8(new FileInputStream(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ScannerUTF8 fromResource(String path) {
        try {
            return new ScannerUTF8(ScannerUTF8.class.getResourceAsStream(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        ScannerUTF8 in = new ScannerUTF8(System.in);
        PrintWriterUTF8 out = new PrintWriterUTF8(System.out);
        while (!in.isExhausted()) {
            out.println(new String(new int[]{in.nextCharacter()}, 0, 1));
        }
        out.close();
    }
}
