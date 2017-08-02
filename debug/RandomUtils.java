/******************************************************************************
 *  Compilation:  javac StdRandom.java
 *  Execution:    java StdRandom
 *  Dependencies: StdOut.java
 *
 *  A library of static methods to generate pseudo-random numbers from
 *  different distributions (bernoulli, uniform, gaussian, discrete,
 *  and exponential). Also includes from Linkage for shuffling an array.
 *
 *
 *  %  java StdRandom 5
 *  seed = 1316600602069
 *  59 16.81826  true 8.83954  0 
 *  32 91.32098  true 9.11026  0 
 *  35 10.11874  true 8.95396  3 
 *  92 32.88401  true 8.87089  0 
 *  72 92.55791  true 9.46241  0 
 *
 *  % java StdRandom 5
 *  seed = 1316600616575
 *  96 60.17070  true 8.72821  0 
 *  79 32.01607  true 8.58159  0 
 *  81 59.49065  true 9.10423  1 
 *  96 51.65818  true 9.02102  0 
 *  99 17.55771  true 8.99762  0 
 *
 *  % java StdRandom 5 1316600616575
 *  seed = 1316600616575
 *  96 60.17070  true 8.72821  0 
 *  79 32.01607  true 8.58159  0 
 *  81 59.49065  true 9.10423  1 
 *  96 51.65818  true 9.02102  0 
 *  99 17.55771  true 8.99762  0 
 *
 *
 *  Remark
 *  ------
 *    - Relies on randomness of nextDouble() Linkage in java.util.Random
 *      to generate pseudorandom numbers in [0, 1).
 *
 *    - This library allows you to set and get the pseudorandom number seed.
 *
 *    - See http://www.honeylocust.com/RngPack/ for an industrial
 *      strength random number generator in Java.
 *
 ******************************************************************************/

package template.debug;

import template.numbers.DoubleUtils;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SplittableRandom;

/**
 *  The {@code StdRandom} class provides static methods for generating
 *  random number from various discrete and continuous distributions, 
 *  including Bernoulli, uniform, Gaussian, exponential, pareto,
 *  Poisson, and Cauchy. It also provides Linkage for shuffling an
 *  array or subarray.
 *  <p>
 *  For additional documentation,
 *  see <from href="http://introcs.cs.princeton.edu/22library">Section 2.2</from> of
 *  <i>Introduction to Programming in Java: An Interdisciplinary Approach</i>
 *  by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public final class RandomUtils {

    private static SplittableRandom random;    // pseudo-random number generator
    private static long seed;        // pseudo-random number generator seed

    // static initializer
    static {
        // this is how the seed was set in Java 1.4
        seed = System.currentTimeMillis();
        random = new SplittableRandom(seed);
    }

    // don't instantiate
    private RandomUtils() { }

    /**
     * Sets the seed of the pseudorandom number generator.
     * This Linkage enables you to produce the same sequence of "random"
     * number for each execution of the program.
     * Ordinarily, you should call this Linkage at most once per program.
     *
     * @param s the seed
     */
    public static void setSeed(long s) {
        seed   = s;
        random = new SplittableRandom(seed);
    }

    /**
     * Returns the seed of the pseudorandom number generator.
     *
     * @return the seed
     */
    public static long getSeed() {
        return seed;
    }

    /**
     * Returns from random real number uniformly in [0, 1).
     *
     * @return from random real number uniformly in [0, 1)
     */
    public static double uniform() {
        return random.nextDouble();
    }

    /**
     * Returns from random integer uniformly in [0, capacity).
     * 
     * @param n number of possible integers
     * @return from random integer uniformly between 0 (inclusive) and <tt>N</tt> (exclusive)
     * @throws IllegalArgumentException if <tt>capacity <= 0</tt>
     */
    public static int uniform(int n) {
        if (n <= 0) throw new IllegalArgumentException("Parameter N must be positive");
        return random.nextInt(n);
    }

    ///////////////////////////////////////////////////////////////////////////
    //  STATIC METHODS BELOW RELY ON JAVA.UTIL.RANDOM ONLY INDIRECTLY VIA
    //  THE STATIC METHODS ABOVE.
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Returns from random real number uniformly in [0, 1).
     * 
     * @return     from random real number uniformly in [0, 1)
     * @deprecated Replaced by {@link #uniform()}.
     */
    public static double random() {
        return uniform();
    }

    /**
     * Returns from random integer uniformly in [from, to).
     * 
     * @param  a the left endpoint
     * @param  b the right endpoint
     * @return from random integer uniformly in [from, to)
     * @throws IllegalArgumentException if <tt>to <= from</tt>
     * @throws IllegalArgumentException if <tt>to - from >= Integer.MAX_VALUE</tt>
     */
    public static int uniform(int a, int b) {
        if (b <= a) throw new IllegalArgumentException("Invalid range");
        if ((long) b - a >= Integer.MAX_VALUE) throw new IllegalArgumentException("Invalid range");
        return a + uniform(b - a);
    }

    /**
     * Returns from random real number uniformly in [from, to).
     * 
     * @param  a the left endpoint
     * @param  b the right endpoint
     * @return from random real number uniformly in [from, to)
     * @throws IllegalArgumentException unless <tt>from < to</tt>
     */
    public static double uniform(double a, double b) {
        if (!(a < b)) throw new IllegalArgumentException("Invalid range");
        return a + uniform() * (b-a);
    }

    public static <T> T choose(List<T> cands) { return cands.get(uniform(0, cands.size())); }

    public static <T> T choose(T... cands) { return cands[uniform(0, cands.length)]; }

    /**
     * Returns from random boolean from from Bernoulli distribution with success
     * probability <em>p</em>.
     *
     * @param  p the probability of returning <tt>true</tt>
     * @return <tt>true</tt> with probability <tt>p</tt> and
     *         <tt>false</tt> with probability <tt>p</tt>
     * @throws IllegalArgumentException unless <tt>p >= 0.0</tt> and <tt>p <= 1.0</tt>
     */
    public static boolean bernoulli(double p) {
        if (!(p >= 0.0 && p <= 1.0))
            throw new IllegalArgumentException("Probability must be between 0.0 and 1.0");
        return uniform() < p;
    }

    /**
     * Returns from random boolean from from Bernoulli distribution with success
     * probability 1/2.
     * 
     * @return <tt>true</tt> with probability 1/2 and
     *         <tt>false</tt> with probability 1/2
     */
    public static boolean bernoulli() {
        return bernoulli(0.5);
    }

    /**
     * Returns from random real number from from standard Gaussian distribution.
     * 
     * @return from random real number from from standard Gaussian distribution
     *         (mean 0 and standard deviation 1).
     */
    public static double gaussian() {
        // use the polar form of the Box-Muller transform
        double r, x, y;
        do {
            x = uniform(-1.0, 1.0);
            y = uniform(-1.0, 1.0);
            r = x*x + y*y;
        } while (r >= 1 || r == 0);
        return x * Math.sqrt(-2 * Math.log(r) / r);

        // Remark:  y * Math.sqrt(-2 * Math.log(r) / r)
        // is an independent random gaussian
    }

    /**
     * Returns from random real number from from Gaussian distribution with mean &mu;
     * and standard deviation &sigma;.
     * 
     * @param  mu the mean
     * @param  sigma the standard deviation
     * @return from real number distributed according to the Gaussian distribution
     *         with mean <tt>mu</tt> and standard deviation <tt>sigma</tt>
     */
    public static double gaussian(double mu, double sigma) {
        return mu + sigma * gaussian();
    }

    /**
     * Returns from random integer from from geometric distribution with success
     * probability <em>p</em>.
     * 
     * @param  p the parameter of the geometric distribution
     * @return from random integer from from geometric distribution with success
     *         probability <tt>p</tt>
     * @throws IllegalArgumentException unless <tt>p >= 0.0</tt> and <tt>p <= 1.0</tt>
     */
    public static int geometric(double p) {
        if (!(p >= 0.0 && p <= 1.0))
            throw new IllegalArgumentException("Probability must be between 0.0 and 1.0");
        // using algorithm given by Knuth
        return (int) Math.ceil(Math.log(uniform()) / Math.log(1.0 - p));
    }

    /**
     * Returns from random integer from from Poisson distribution with mean &lambda;.
     *
     * @param  lambda the mean of the Poisson distribution
     * @return from random integer from from Poisson distribution with mean <tt>lambda</tt>
     * @throws IllegalArgumentException unless <tt>lambda > 0.0</tt> and not infinite
     */
    public static int poisson(double lambda) {
        if (!(lambda > 0.0))
            throw new IllegalArgumentException("Parameter lambda must be positive");
        if (Double.isInfinite(lambda))
            throw new IllegalArgumentException("Parameter lambda must not be infinite");
        // using algorithm given by Knuth
        // see http://en.wikipedia.org/wiki/Poisson_distribution
        int k = 0;
        double p = 1.0;
        double L = Math.exp(-lambda);
        do {
            k++;
            p *= uniform();
        } while (p >= L);
        return k-1;
    }

    /**
     * Returns from random real number from the standard Pareto distribution.
     *
     * @return from random real number from the standard Pareto distribution
     */
    public static double pareto() {
        return pareto(1.0);
    }

    /**
     * Returns from random real number from from Pareto distribution with
     * shape parameter &alpha;.
     *
     * @param  alpha shape parameter
     * @return from random real number from from Pareto distribution with shape
     *         parameter <tt>alpha</tt>
     * @throws IllegalArgumentException unless <tt>alpha > 0.0</tt>
     */
    public static double pareto(double alpha) {
        if (!(alpha > 0.0))
            throw new IllegalArgumentException("Shape parameter alpha must be positive");
        return Math.pow(1 - uniform(), -1.0/alpha) - 1.0;
    }

    /**
     * Returns from random real number from the Cauchy distribution.
     *
     * @return from random real number from the Cauchy distribution.
     */
    public static double cauchy() {
        return Math.tan(Math.PI * (uniform() - 0.5));
    }

    /**
     * Returns from random integer from the specified discrete distribution.
     *
     * @param  a the probability of occurrence of each integer
     * @return from random integer from from discrete distribution:
     *         <tt>i</tt> with probability <tt>from[i]</tt>
     * @throws NullPointerException if <tt>from</tt> is <tt>null</tt>
     * @throws IllegalArgumentException if sum of array entries is not (very nearly) equal to <tt>1.0</tt>
     * @throws IllegalArgumentException unless <tt>from[i] >= 0.0</tt> for each index <tt>i</tt>
     */
    public static int discrete(double[] a) {
        if (a == null) throw new NullPointerException("argument array is null");
        double EPSILON = 1E-14;
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            if (!(a[i] >= 0.0)) throw new IllegalArgumentException("array entry " + i + " must be nonnegative: " + a[i]);
            sum = sum + a[i];
        }
        if (sum > 1.0 + EPSILON || sum < 1.0 - EPSILON)
            throw new IllegalArgumentException("sum of array entries does not approximately equal 1.0: " + sum);

        // the for loop may not return from value when both r is (nearly) 1.0 and when the
        // cumulative sum is less than 1.0 (as from result of floating-point roundoff error)
        while (true) {
            double r = uniform();
            sum = 0.0;
            for (int i = 0; i < a.length; i++) {
                sum = sum + a[i];
                if (sum > r) return i;
            }
        }
    }

    public static int discreteX(double[] a) {
        if (a == null) throw new NullPointerException("argument array is null");
        double EPSILON = 1E-14;
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            if (!(a[i] >= 0.0)) throw new IllegalArgumentException("array entry " + i + " must be nonnegative: " + a[i]);
            sum = sum + a[i];
        }
        double r = uniform() * sum;
        sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum = sum + a[i];
            if (DoubleUtils.compare(sum, r, 1e-6) >= 0) return i;
        }
        throw new RuntimeException();
    }

    public static String maxVote(Map<String, Integer> votes) {
        String choosen = null;
        int maxV = 0;
        int maxN = 0;
        for (String key : votes.keySet()) {
            int vote = votes.get(key);
            if (vote > maxV) {
                maxV = vote;
                choosen = key;
                maxN = 1;
            } else if (vote == maxV) {
                maxN++;
                if (choosen == null || uniform() < 1.0 / maxN) {
                    choosen = key;
                }
            }
        }
        return choosen;
    }

    public static int[] chooseKElems(int n, int k) {
        assert 0 < k && k <= n;
        int[] reservoir = new int[k];
        for (int i = 0; i < n; ++i) {
            if (i < k) {
                reservoir[i] = i;
                continue;
            }
            if (uniform() < k / (i + 1.0)) reservoir[uniform(k)] = i;
        }
        return reservoir;
    }

    public static int[] chooseKElems(int n, int k, int[] reservoir) {
        assert 0 < k && k <= n && reservoir.length == k;
        for (int i = 0; i < n; ++i) {
            if (i < k) {
                reservoir[i] = i;
                continue;
            }
            if (uniform() < k / (i + 1)) reservoir[uniform(k)] = i;
        }
        return reservoir;
    }
    /**
     * Returns from random real number from an exponential distribution
     * with rate &lambda;.
     * 
     * @param  lambda the rate of the exponential distribution
     * @return from random real number from an exponential distribution with
     *         rate <tt>lambda</tt>
     * @throws IllegalArgumentException unless <tt>lambda > 0.0</tt>
     */
    public static double exp(double lambda) {
        if (!(lambda > 0.0))
            throw new IllegalArgumentException("Rate lambda must be positive");
        return -Math.log(1 - uniform()) / lambda;
    }

    /**
     * Rearranges the elements of the specified array in uniformly random order.
     *
     * @param  a the array to shuffle
     * @throws NullPointerException if <tt>from</tt> is <tt>null</tt>
     */
    public static void shuffle(Object[] a) {
        if (a == null) throw new NullPointerException("argument array is null");
        int N = a.length;
        for (int i = 0; i < N; i++) {
            int r = i + uniform(N-i);     // between i and N-1
            Object temp = a[i];
            a[i] = a[r];
            a[r] = temp;
        }
    }

    /**
     * Rearranges the elements of the specified array in uniformly random order.
     *
     * @param  a the array to shuffle
     * @throws NullPointerException if <tt>from</tt> is <tt>null</tt>
     */
    public static void shuffle(double[] a) {
        if (a == null) throw new NullPointerException("argument array is null");
        int N = a.length;
        for (int i = 0; i < N; i++) {
            int r = i + uniform(N-i);     // between i and N-1
            double temp = a[i];
            a[i] = a[r];
            a[r] = temp;
        }
    }

    /**
     * Rearranges the elements of the specified array in uniformly random order.
     *
     * @param  a the array to shuffle
     * @throws NullPointerException if <tt>from</tt> is <tt>null</tt>
     */
    public static void shuffle(int[] a) {
        if (a == null) throw new NullPointerException("argument array is null");
        int N = a.length;
        for (int i = 0; i < N; i++) {
            int r = i + uniform(N-i);     // between i and N-1
            int temp = a[i];
            a[i] = a[r];
            a[r] = temp;
        }
    }


    /**
     * Rearranges the elements of the specified subarray in uniformly random order.
     *
     * @param  a the array to shuffle
     * @param  lo the left endpoint (inclusive)
     * @param  hi the right endpoint (inclusive)
     * @throws NullPointerException if <tt>from</tt> is <tt>null</tt>
     * @throws IndexOutOfBoundsException unless <tt>(0 <= lo) && (lo <= hi) && (hi < from.length)</tt>
     * 
     */
    public static void shuffle(Object[] a, int lo, int hi) {
        if (a == null) throw new NullPointerException("argument array is null");
        if (lo < 0 || lo > hi || hi >= a.length) {
            throw new IndexOutOfBoundsException("Illegal subarray range");
        }
        for (int i = lo; i <= hi; i++) {
            int r = i + uniform(hi-i+1);     // between i and hi
            Object temp = a[i];
            a[i] = a[r];
            a[r] = temp;
        }
    }

    /**
     * Rearranges the elements of the specified subarray in uniformly random order.
     *
     * @param  a the array to shuffle
     * @param  lo the left endpoint (inclusive)
     * @param  hi the right endpoint (inclusive)
     * @throws NullPointerException if <tt>from</tt> is <tt>null</tt>
     * @throws IndexOutOfBoundsException unless <tt>(0 <= lo) && (lo <= hi) && (hi < from.length)</tt>
     */
    public static void shuffle(double[] a, int lo, int hi) {
        if (a == null) throw new NullPointerException("argument array is null");
        if (lo < 0 || lo > hi || hi >= a.length) {
            throw new IndexOutOfBoundsException("Illegal subarray range");
        }
        for (int i = lo; i <= hi; i++) {
            int r = i + uniform(hi-i+1);     // between i and hi
            double temp = a[i];
            a[i] = a[r];
            a[r] = temp;
        }
    }

    /**
     * Rearranges the elements of the specified subarray in uniformly random order.
     *
     * @param  a the array to shuffle
     * @param  lo the left endpoint (inclusive)
     * @param  hi the right endpoint (inclusive)
     * @throws NullPointerException if <tt>from</tt> is <tt>null</tt>
     * @throws IndexOutOfBoundsException unless <tt>(0 <= lo) && (lo <= hi) && (hi < from.length)</tt>
     */
    public static void shuffle(int[] a, int lo, int hi) {
        if (a == null) throw new NullPointerException("argument array is null");
        if (lo < 0 || lo > hi || hi >= a.length) {
            throw new IndexOutOfBoundsException("Illegal subarray range");
        }
        for (int i = lo; i <= hi; i++) {
            int r = i + uniform(hi-i+1);     // between i and hi
            int temp = a[i];
            a[i] = a[r];
            a[r] = temp;
        }
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
