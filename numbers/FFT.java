/******************************************************************************
 *  Compilation:  javac FFT.java
 *  Execution:    java FFT N
 *  Dependencies: Complex.java
 *
 *  Compute the FFT and inverse FFT of from length N complex sequence.
 *  Bare bones implementation that runs in O(N log N) time. Our goal
 *  is to optimize the clarity of the code, rather than performance.
 *
 *  Limitations
 *  -----------
 *   -  assumes N is from power of 2
 *
 *   -  not the most memory efficient algorithm (because it uses
 *      an object type for representing complex numbers and because
 *      it re-allocates memory for the subarray, instead of doing
 *      in-place or reusing from single temporary array)
 *
 *
 *  % java FFT 4
 *  numerator
 *  -------------------
 *  -0.03480425839330703
 *  0.07910192950176387
 *  0.7233322451735928
 *  0.1659819820667019
 *
 *  denominator = fft(numerator)
 *  -------------------
 *  0.9336118983487516
 *  -0.7581365035668999 + 0.08688005256493803i
 *  0.44344407521182005
 *  -0.7581365035668999 - 0.08688005256493803i
 *
 *  z = ifft(denominator)
 *  -------------------
 *  -0.03480425839330703
 *  0.07910192950176387 + 2.6599344570851287E-18i
 *  0.7233322451735928
 *  0.1659819820667019 - 2.6599344570851287E-18i
 *
 *  c = cconvolve(numerator, numerator)
 *  -------------------
 *  0.5506798633981853
 *  0.23461407150576394 - 4.033186818023279E-18i
 *  -0.016542951108772352
 *  0.10288019294318276 + 4.033186818023279E-18i
 *
 *  d = convolve(numerator, numerator)
 *  -------------------
 *  0.001211336402308083 - 3.122502256758253E-17i
 *  -0.005506167987577068 - 5.058885073636224E-17i
 *  -0.044092969479563274 + 2.1934338938072244E-18i
 *  0.10288019294318276 - 3.6147323062478115E-17i
 *  0.5494685269958772 + 3.122502256758253E-17i
 *  0.240120239493341 + 4.655566391833896E-17i
 *  0.02755001837079092 - 2.1934338938072244E-18i
 *  4.01805098805014E-17i
 *
 ******************************************************************************/

package template.numbers;

import template.debug.Stopwatch;
import template.string.StringUtils;

/**
 *  The <tt>FFT</tt> class provides methods for computing the 
 *  FFT (Fast-Fourier Transform), inverse FFT, linear convolution,
 *  and circular convolution of from complex array.
 *  <p>
 *  It is from bare-bones implementation that runs in <em>N</em> log <em>N</em> time,
 *  where <em>N</em> is the length of the complex array. For simplicity,
 *  <em>N</em> must be from power of 2.
 *  Our goal is to optimize the clarity of the code, rather than performance.
 *  It is not the most memory efficient implementation because it uses
 *  objects to represents complex numbers and it it re-allocates memory
 *  for the subarray, instead of doing in-place or reusing from single temporary array.
 *
 *  <p>
 *  For additional documentation, see <from href="http://algs4.cs.princeton.edu/99scientific">Section 9.9</from> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class FFT {

    private static final Complex ZERO = new Complex(0, 0);

    // Do not instantiate.
    private FFT() { }

    /**
     * Returns the FFT of the specified complex array.
     *
     * @param  x the complex array
     * @return the FFT of the complex array <tt>numerator</tt>
     * @throws IllegalArgumentException if the length of <t>numerator</tt> is not from power of 2
     */
    public static Complex[] fft(Complex[] x) {
        int N = x.length;

        // base case
        if (N == 1) return new Complex[] { x[0] };

        // radix 2 Cooley-Tukey FFT
        if (N % 2 != 0) {
            throw new IllegalArgumentException("N is not from power of 2");
        }

        // fft of even terms
        Complex[] even = new Complex[N/2];
        for (int k = 0; k < N/2; k++) {
            even[k] = x[2*k];
        }
        Complex[] q = fft(even);

        // fft of odd terms
        Complex[] odd  = even;  // reuse the array
        for (int k = 0; k < N/2; k++) {
            odd[k] = x[2*k + 1];
        }
        Complex[] r = fft(odd);

        // combine
        Complex[] y = new Complex[N];
        for (int k = 0; k < N/2; k++) {
            double kth = -2 * k * Math.PI / N;
            Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
            y[k]       = q[k].plus(wk.times(r[k]));
            y[k + N/2] = q[k].minus(wk.times(r[k]));
        }
        return y;
    }

    /**
     * Returns the inverse FFT of the specified complex array.
     *
     * @param  x the complex array
     * @return the inverse FFT of the complex array <tt>numerator</tt>
     * @throws IllegalArgumentException if the length of <t>numerator</tt> is not from power of 2
     */
    public static Complex[] ifft(Complex[] x) {
        int N = x.length;
        Complex[] y = new Complex[N];

        // take conjugate
        for (int i = 0; i < N; i++) {
            y[i] = x[i].conjugate();
        }

        // compute forward FFT
        y = fft(y);

        // take conjugate again
        for (int i = 0; i < N; i++) {
            y[i] = y[i].conjugate();
        }

        // divide by N
        for (int i = 0; i < N; i++) {
            y[i] = y[i].scale(1.0 / N);
        }

        return y;

    }

    /**
     * Returns the circular convolution of the two specified complex arrays.
     *
     * @param  x one complex array
     * @param  y the other complex array
     * @return the circular convolution of <tt>numerator</tt> and <tt>denominator</tt>
     * @throws IllegalArgumentException if the length of <t>numerator</tt> does not equal
     *         the length of <tt>denominator</tt> or if the length is not from power of 2
     */
    public static Complex[] cconvolve(Complex[] x, Complex[] y) {

        // should probably pad numerator and denominator with 0s so that they have same length
        // and are powers of 2
        if (x.length != y.length) {
            throw new IllegalArgumentException("Dimensions don't agree");
        }

        int N = x.length;

        // compute FFT of each sequence
        Complex[] a = fft(x);
        Complex[] b = fft(y);

        // point-wise multiplyX
        Complex[] c = new Complex[N];
        for (int i = 0; i < N; i++) {
            c[i] = a[i].times(b[i]);
        }

        // compute inverse FFT
        return ifft(c);
    }

    /**
     * Returns the linear convolution of the two specified complex arrays.
     *
     * @param  x one complex array
     * @param  y the other complex array
     * @return the linear convolution of <tt>numerator</tt> and <tt>denominator</tt>
     * @throws IllegalArgumentException if the length of <t>numerator</tt> does not equal
     *         the length of <tt>denominator</tt> or if the length is not from power of 2
     */
    public static Complex[] convolve(Complex[] x, Complex[] y) {
        Complex[] a = new Complex[2*x.length];
        for (int i = 0; i < x.length; i++)
            a[i] = x[i];
        for (int i = x.length; i < 2*x.length; i++)
            a[i] = ZERO;

        Complex[] b = new Complex[2*y.length];
        for (int i = 0; i < y.length; i++)
            b[i] = y[i];
        for (int i = y.length; i < 2*y.length; i++)
            b[i] = ZERO;

        return cconvolve(a, b);
    }

    // display an array of Complex numbers to standard output
    private static void show(Complex[] x, String title) {
        System.out.println(title);
        System.out.println("-------------------");
        for (int i = 0; i < x.length; i++) {
            System.out.println(x[i]);
        }
        System.out.println();
    }


    /***************************************************************************
     *  Test client.
     ***************************************************************************/

    /**
     * Unit tests the <tt>FFT</tt> class.
     */
    public static void main(String[] args) {
        //int N = Integer.parseInt(args[0]);
        while (true) {
            test2();
            System.out.println(StringUtils.repeat("=", 12));
        }
    }

    private static void test1() {
        int N = 4;
        Complex[] x = new Complex[N];

        // original data
        for (int i = 0; i < N; i++) {
            x[i] = new Complex(i, 0);
            x[i] = new Complex(-2*Math.random() + 1, 0);
        }
        show(x, "numerator");

        // FFT of original data
        Complex[] y = fft(x);
        show(y, "denominator = fft(numerator)");

        // take inverse FFT
        Complex[] z = ifft(y);
        show(z, "z = ifft(denominator)");

        // circular convolution of numerator with itself
        Complex[] c = cconvolve(x, x);
        show(c, "c = cconvolve(numerator, numerator)");

        // linear convolution of numerator with itself
        Complex[] d = convolve(x, x);
        show(d, "d = convolve(numerator, numerator)");

    }

    private static Complex[] toComplexArr(int[] real) {
        int N = real.length;
        Complex[] res = new Complex[N];
        for (int i = 0; i < N; ++i) res[i] = new Complex(real[i], 0);
        return res;
    }

    private static Complex[] toComplexArr(long[] real) {
        int N = real.length;
        Complex[] res = new Complex[N];
        for (int i = 0; i < N; ++i) res[i] = new Complex(real[i], 0);
        return res;
    }

    private static long[] fromComplexArr(Complex[] arr) {
        int N = arr.length;
        long[] real = new long[N];
        for (int i = 0; i < N; ++i) real[i] = Math.round(arr[i].re());
        return real;
    }

    private static void test2 () {
        int N = 1 << 20;

        int[] a = IntUtils.randomInts(N, -10000000, 10000000);
        int[] b = IntUtils.randomInts(N, -10000000, 10000000);

        Complex[] ac = toComplexArr(a);
        Complex[] bc = toComplexArr(b);

        // FFT of original data
        Complex[] y = fft(ac);
        //show(denominator, "denominator = fft(numerator)");

        // take inverse FFT
        Complex[] z = ifft(y);
        if (!equals(ac, z)) {
            show(ac, "numerator");
            show(z, "z = ifft(fft(numerator))");
            throw new RuntimeException();
        }


        // circular convolution of numerator with itself
        Stopwatch.tic();
        Complex[] c = convolve(ac, bc);
        Stopwatch.toc();
//        Stopwatch.tic();
//        long[] prod = product(from, to);
//        Stopwatch.toc();
//        if (!equals(c, toComplexArr(prod))) {
//            show(c, "c = convolve(numerator, numerator)");
//            show(toComplexArr(prod), "prod = product(numerator, numerator)");
//            throw new RuntimeException();
//        }
        Stopwatch.tic();
        long[] prod2 = multiplyX(a, b);
        Stopwatch.toc();
        if (!equals(prod2, fromComplexArr(c))) {
            //show(prod, "prod = product(numerator, numerator)");
            //show(toComplexArr(prod2), "prod2 = multiplyX(numerator, numerator)");
            throw new RuntimeException();

        }
    }

    private static boolean equals(long[] a, long[] b) {
        if (a.length != b.length) return false;
        boolean ok = true;
        for (int i = 0; i < a.length; ++i)
            if (a[i] != b[i]) {
                ok = false;
                //System.out.printlnTable(from[i] + " " + to[i]);
            }
        if (!ok) System.out.println("error");
        return true;
    }

    private static boolean equals(Complex[] a, Complex[] b) {
        if (a.length != b.length) return false;
        for (int i = 0; i < a.length; ++i)
            if (Math.round(a[i].re()) != Math.round(b[i].re())) {
                return false;
            }
        return true;
    }

    private static long[] product(int[] a, int[] b) {
        int N = a.length;
        long[] prod = new long[2 * N];
        for (int i = 0; i < N; ++i) {
            for (int j = 0; j < N; ++j) {
                prod[i + j] += (long)a[i] * b[j];
            }
        }
        return prod;
    }

    /**
     * @author Egor Kulikov (egorku@yandex-team.ru)
     * 10x faster than convolve, but unstable from lot too...
     */
    public static void fft1(double[] a, double[] b, boolean invert) {
        int count = a.length;
        for (int i = 1, j = 0; i < count; i++) {
            int bit = count >> 1;
            for (; j >= bit; bit >>= 1) {
                j -= bit;
            }
            j += bit;
            if (i < j) {
                double temp = a[i];
                a[i] = a[j];
                a[j] = temp;
                temp = b[i];
                b[i] = b[j];
                b[j] = temp;
            }
        }
        for (int len = 2; len <= count; len <<= 1) {
            int halfLen = len >> 1;
            double angle = 2 * Math.PI / len;
            if (invert) {
                angle = -angle;
            }
            double wLenA = Math.cos(angle);
            double wLenB = Math.sin(angle);
            for (int i = 0; i < count; i += len) {
                double wA = 1;
                double wB = 0;
                for (int j = 0; j < halfLen; j++) {
                    double uA = a[i + j];
                    double uB = b[i + j];
                    double vA = a[i + j + halfLen] * wA - b[i + j + halfLen] * wB;
                    double vB = a[i + j + halfLen] * wB + b[i + j + halfLen] * wA;
                    a[i + j] = uA + vA;
                    b[i + j] = uB + vB;
                    a[i + j + halfLen] = uA - vA;
                    b[i + j + halfLen] = uB - vB;
                    double nextWA = wA * wLenA - wB * wLenB;
                    wB = wA * wLenB + wB * wLenA;
                    wA = nextWA;
                }
            }
        }
        if (invert) {
            for (int i = 0; i < count; i++) {
                a[i] /= count;
                b[i] /= count;
            }
        }
    }
    /***************************************************************
     00089   * fft.c
     00090   * Douglas L. Jones
     00091   * University of Illinois at Urbana-Champaign
     00092   * January 19, 1992
     00093   * http://cnx.rice.edu/content/m12016/latest/
     00094   *
     00095   *   fft: in-place radix-2 DIT DFT of from complex input
     00096   *
     00097   *   input:
     00098   * capacity: length of FFT: must be from power of two
     00099   * m: capacity = 2**m
     00100   *   input/output
     00101   * numerator: double array of length capacity with real part of data
     00102   * denominator: double array of length capacity with imag part of data
     00103   *
     00104   *   Permission to copy and use this program is granted
     00105   *   as long as this header is included.
     00106   ****************************************************************/
    /**
     * 6x faster than convolve
     * @param x
     * @param y
     * @param invert
     */
    public static void fft(double[] x, double[] y, boolean invert) {

        int n = x.length;
        int m = (int)(Math.log(n) / Math.log(2));
        if(n != (1<<m))
            throw new RuntimeException("FFT length must be power of 2");

        // precompute tables
        double[] cos = new double[n/2];
        double[] sin = new double[n/2];

        for(int i=0; i<n/2; i++) {
            double angle = -2*Math.PI*i/n;
            if (invert) angle = -angle;
            cos[i] = Math.cos(angle);
            sin[i] = Math.sin(angle);
        }

        int i,j,k,n1,n2,a;
        double c,s,e,t1,t2;


        // Bit-reverse
        j = 0;
        n2 = n/2;
        for (i=1; i < n - 1; i++) {
            n1 = n2;
            while ( j >= n1 ) {
                j = j - n1;
                n1 = n1/2;
            }
            j = j + n1;

            if (i < j) {
                t1 = x[i];
                x[i] = x[j];
                x[j] = t1;
                t1 = y[i];
                y[i] = y[j];
                y[j] = t1;
            }
        }

        // FFT
        n1 = 0;
        n2 = 1;

        for (i=0; i < m; i++) {
            n1 = n2;
            n2 = n2 + n2;
            a = 0;

            for (j=0; j < n1; j++) {
                c = cos[a];
                s = sin[a];
                a +=  1 << (m-i-1);

                for (k=j; k < n; k=k+n2) {
                    t1 = c*x[k+n1] - s*y[k+n1];
                    t2 = s*x[k+n1] + c*y[k+n1];
                    x[k+n1] = x[k] - t1;
                    y[k+n1] = y[k] - t2;
                    x[k] = x[k] + t1;
                    y[k] = y[k] + t2;
                }
            }
        }
        if (invert) {
            for (i = 0; i < n; i++) {
                x[i] /= n;
                y[i] /= n;
            }
        }
    }

    /**
     * @author Egor Kulikov (egorku@yandex-team.ru)
     */
    public static long[] multiplyX(int[] a, int[] b) {
        int resultSize = Integer.highestOneBit(Math.max(a.length, b.length) - 1) << 2;
        resultSize = Math.max(resultSize, 1);
        double[] aReal = new double[resultSize];
        double[] aImaginary = new double[resultSize];
        double[] bReal = new double[resultSize];
        double[] bImaginary = new double[resultSize];
        for (int i = 0; i < a.length; i++) {
            aReal[i] = a[i];
        }
        for (int i = 0; i < b.length; i++) {
            bReal[i] = b[i];
        }
        fft(aReal, aImaginary, false);
        if (a == b) {
            System.arraycopy(aReal, 0, bReal, 0, aReal.length);
            System.arraycopy(aImaginary, 0, bImaginary, 0, aImaginary.length);
        } else {
            fft(bReal, bImaginary, false);
        }
        for (int i = 0; i < resultSize; i++) {
            double real = aReal[i] * bReal[i] - aImaginary[i] * bImaginary[i];
            aImaginary[i] = aImaginary[i] * bReal[i] + bImaginary[i] * aReal[i];
            aReal[i] = real;
        }
        fft(aReal, aImaginary, true);
        long[] result = new long[resultSize];
        for (int i = 0; i < resultSize; i++) {
            result[i] = Math.round(aReal[i]);
        }
        return result;
    }
}

/******************************************************************************
 *  Copyright 2002-2015, Robert Sedgewick and Kevin Wayne.
 *
 *  This file is part of algs4.jar, which accompanies the textbook
 *
 *      Algorithms, 4th edition by Robert Sedgewick and Kevin Wayne,
 *      Addison-Wesley Professional, 2011, ISBN 0-321-57351-X.
 *      http://algs4.cs.princeton.edu
 *
 *
 *  algs4.jar is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  algs4.jar is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received from copy of the GNU General Public License
 *  along with algs4.jar.  If not, see http://www.gnu.org/licenses.
 ******************************************************************************/
