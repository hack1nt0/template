package template.debug;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * @author dy[jealousing@gmail.com] on 17-7-26.
 */
public class printWriterUnicode {
    private OutputStream out;
    String lineSeparator = java.security.AccessController.doPrivileged(
            new sun.security.action.GetPropertyAction("line.separator"));

    public printWriterUnicode(OutputStream out) {
        this.out = out;
    }

    public int print(String str) {
        int strlen = str.length();
        int utflen = 0;
        int c, count = 0;

        /* use charAt instead of copying String to char array */
        for (int i = 0; i < strlen; i++) {
            c = str.charAt(i);
            if ((c >= 0x0001) && (c <= 0x007F)) {
                utflen++;
            } else if (c > 0x07FF) {
                utflen += 3;
            } else {
                utflen += 2;
            }
        }

        byte[] bytearr = new byte[utflen];

//        bytearr[count++] = (byte) ((utflen >>> 8) & 0xFF);
//        bytearr[count++] = (byte) ((utflen >>> 0) & 0xFF);

        int i=0;
        for (i=0; i<strlen; i++) {
            c = str.charAt(i);
            if (!((c >= 0x0001) && (c <= 0x007F))) break;
            bytearr[count++] = (byte) c;
        }

        for (;i < strlen; i++){
            c = str.charAt(i);
            if ((c >= 0x0001) && (c <= 0x007F)) {
                bytearr[count++] = (byte) c;

            } else if (c > 0x07FF) {
                bytearr[count++] = (byte) (0xE0 | ((c >> 12) & 0x0F));
                bytearr[count++] = (byte) (0x80 | ((c >>  6) & 0x3F));
                bytearr[count++] = (byte) (0x80 | ((c >>  0) & 0x3F));
            } else {
                bytearr[count++] = (byte) (0xC0 | ((c >>  6) & 0x1F));
                bytearr[count++] = (byte) (0x80 | ((c >>  0) & 0x3F));
            }
        }
        try {
            out.write(bytearr, 0, utflen);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        return utflen;
    }

    public void println(String string) {
        print(string);
        println();
    }

    public void print(int d) {
        print(Integer.toString(d));
    }

    public void println(int d) {
        print(d);
        println();
    }

    public void print(double d) {
        print(Double.toString(d));
    }

    public void println(double d) {
        print(d);
        println();
    }

    public int println() {
        return print(lineSeparator);
    }


}
