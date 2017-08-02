package template.debug;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * PrintWriter with only UTF-8 supported.
 *
 * @author dy[jealousing@gmail.com] on 17-7-26.
 */
public class PrintWriterUTF8 {
    private OutputStream out;
    String lineSeparator = java.security.AccessController.doPrivileged(
            new sun.security.action.GetPropertyAction("line.separator"));

    public PrintWriterUTF8(OutputStream out) {
        this.out = new BufferedOutputStream(out);
    }

    public int print(String str) {
//        int strlen = str.length();
//        int utflen = 0;
//        int count = 0;
//
//        /* use charAt instead of copying String to char array */
//        for (int i = 0; i < strlen; i++) {
//            char c = str.charAt(i);
//            if (Character.isHighSurrogate(c)) {
//                if (i + 1 >= strlen) throw new InputMismatchException();
//                c = str.charAt(++i);
//                if (!Character.isLowSurrogate(c)) throw new InputMismatchException();
//                utflen += 4;
//                continue;
//            }
//            if (0x0000 <= c && c <= 0x007F) {
//                utflen += 1;
//            } else if (0x0080 <= c && c <= 0x07FF) {
//                utflen += 2;
//            } else if (0x0800 <= c && c <= 0xFFFF){
//                utflen += 3;
//            }
//        }
//
//        byte[] bytearr = new byte[utflen];
//
////        bytearr[count++] = (byte) ((utflen >>> 8) & 0xFF);
////        bytearr[count++] = (byte) ((utflen >>> 0) & 0xFF);
//
//        int i=0;
//        for (i=0; i<strlen; i++) {
//            char c = str.charAt(i);
//            if (!((c >= 0x0001) && (c <= 0x007F))) break;
//            bytearr[count++] = (byte) c;
//        }
//
//        for (;i < strlen; i++){
//            char c = str.charAt(i);
//            if (Character.isHighSurrogate(c)) {
//                char c2 = str.charAt(++i);
//                Character.toCodePoint(c, c2);
//                continue;
//            }
//            if ((c >= 0x0001) && (c <= 0x007F)) {
//                bytearr[count++] = (byte) c;
//
//            } else if (c > 0x07FF) {
//                bytearr[count++] = (byte) (0xE0 | ((c >> 12) & 0x0F));
//                bytearr[count++] = (byte) (0x80 | ((c >>  6) & 0x3F));
//                bytearr[count++] = (byte) (0x80 | ((c >>  0) & 0x3F));
//            } else {
//                bytearr[count++] = (byte) (0xC0 | ((c >>  6) & 0x1F));
//                bytearr[count++] = (byte) (0x80 | ((c >>  0) & 0x3F));
//            }
//
//        }

        byte[] bytearr = str.getBytes(StandardCharsets.UTF_8);
        try {
            out.write(bytearr, 0, bytearr.length);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        return bytearr.length;
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


    public void close() {
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
