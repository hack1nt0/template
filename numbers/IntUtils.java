package template.numbers;

import template.collection.IntArrayList;
import template.collection.tuple.Tuple2;
import template.collection.tuple.Tuple3;
import template.debug.RandomUtils;

import java.math.BigInteger;
import java.util.*;
import java.util.function.*;
import java.util.stream.IntStream;

/**
 * Created by dy on 16-10-8.
 *
 * Number Theory(NT) utils.
 *
 * This is a world of mod, all values return are no-negative.
 *
 */
public class IntUtils {

    public static long modulus = Long.MAX_VALUE;

    public static long ensurePositive(long a) {
        if (a < 0) return (a % modulus + modulus) % modulus;
        return a;
    }

    // what about from < 0 or to < 0 ?
    public static long lcm(long a, long b) {
        long d = gcd(a, b);
        if (d == 0) return 0;
        BigInteger ans = BigInteger.valueOf(a).divide(BigInteger.valueOf(d)).multiply(BigInteger.valueOf(b));
        if (ans.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0) {
            System.err.println("lcm is out of bound!");
        }
        return ans.mod(BigInteger.valueOf(modulus)).longValue();
    }

    public static long gcd(long a, long b) {
        if (b == 0) return Math.abs(a);
        return gcd(b, a % b);
    }

    public static long mul(long a, long b) {
        a %= modulus;
        b %= modulus;
        return ensurePositive(a * b % modulus);
    }

    public static long mul(long a, long b, long c) {
        return mul(a, mul(b, c));
    }

    public static long mul(long a, long b, long c, long d) {
        return mul(a, mul(b, c, d));
    }

    public static long mul(long a, long b, long c, long d, long e) {
        return mul(a, mul(b, c, d, e));
    }

    public static long add(long a, long b) {
        a %= modulus;
        b %= modulus;
        return ensurePositive((a + b) % modulus);
    }

    public static long add(long a, long b, long c) {
        return add(a, add(b, c));
    }

    public static long add(long a, long b, long c, long d) {
        return add(a, add(b, c, d));
    }

    public static long add(long a, long b, long c, long d, long e) {
        return add(a, add(b, c, d, e));
    }

//    public static long div(long a, long b) {
//        return multiply(a, inv(b));
//    }
//
//    public static long div(long a, long b, long c) {
//        return multiply(a, inv(b), inv(c));
//    }
//
//    public static long div(long a, long b, long c, long d) {
//        return multiply(a, inv(b), inv(c), inv(d));
//    }
//
//    public static long div(long a, long b, long c, long d, long e) {
//        return multiply(a, inv(b), inv(c), inv(d), inv(e));
//    }

//    public static long div(long a, long b, long[] modReversal) {
//        return multiply(a, modReversal[(int)b]);
//    }
//
//    public static long div(long a, long b, long c, long[] inv) {
//        return multiply(a, inv[(int)b], inv[(int)c]);
//    }
//
//    public static long div(long a, long b, long c, long d, long[] inv) {
//        return multiply(a, inv[(int)b], inv[(int)c], inv[(int)d]);
//    }
//
//    public static long div(long a, long b, long c, long d, long e, long[] inv) {
//        return multiply(a, inv[(int)b], inv[(int)c], inv[(int)d], inv[(int)e]);
//    }

    //when modulus is from prime
    public static long modReverse(long a) {
        assert isPrime(modulus);
        a %= modulus;
        return pow(a, modulus - 2);
    }

    public static boolean isPrime(long a) {
        //approximate first, brute force second.

        //the approximate check is very consuming
//        if (!(BigInteger.valueOf(from).isProbablePrime(100)))
//            return false;

        if (a <= 1) return false;
        if (a == 2) return true;
        for (long d = 2; d * d <= a; ++d) {
            if (a % d == 0) return false;
        }
        return true;
    }

    //when modulus is not from prime
    public static long modReverse(long a, long MOD) {
        if (gcd(a, MOD) != 1) throw new RuntimeException("modInverse(" + a + "," + MOD + ") not exist.");
        long x = extgcd(a, MOD).getFirst();
        return (MOD + x % MOD) % MOD;
    }

    public static Tuple3<Long, Long, Long> extgcd(long a, long b) {
        if (b == 0) return new Tuple3<>(1L, 0L, a);
        Tuple3<Long, Long, Long> nxt = extgcd(b, a % b);
        long x, y, gcd;
        x = nxt.getSecond();
        y = nxt.getFirst() - a / b * x;
        gcd = nxt.getThird();

        return new Tuple3<>(x, y, gcd);
    }

    /**
     * Code from Programming Contest Challenge Book
     * @param A
     * @param B
     * @param Mod
     * @return
     */
    public static Tuple2<Long, Long> linearCongruence(long[] A, long[] B, long[] Mod) {
        long x = 0, mod = 1;
        int n = A.length;
        for (int i = 0; i < n; ++i) {
            long a = A[i] * mod, b = B[i] - A[i] * x, d = gcd(Mod[i], a);
            if (b % d != 0) throw new RuntimeException(); // no solution
            long t = b / d * modReverse(a / d, Mod[i] / d) % (Mod[i] / d);
            x += mod * t;
            mod *= Mod[i] / d;
        }
        return new Tuple2<>(x % mod, mod);
    }

    public static long pow(long a, long p) {
        long res = 1;
        long acc = a;
        while (p > 0) {
            if ((p & 1) != 0) res = (res * acc) % modulus;
            p >>= 1;
            acc = (acc * acc) % modulus;
        }
        return res;
    }

    public static long randPrime(int nbits) {
        return BigInteger.probablePrime(nbits, new Random()).longValue();
    }



    public static List<Integer> primes(int lessThan) {
        assert lessThan >= 2;
        List<Integer> res = new ArrayList<>();
        boolean[] isPrime = new boolean[lessThan];
        Arrays.fill(isPrime, true);
        // TODO: 17-1-18
        isPrime[1] = false;
        for (int i = 2; i < lessThan; ++i) {
            if (!isPrime[i]) continue;
            res.add(i);
            for (int j = i * 2; j < lessThan; j += i) isPrime[j] = false;
        }
        return res;
    }

    /**
     * The accumulating predict func may cause StackOverFlow error.
     * @return
     */
    public static IntStream primes() {
        IntPredicate[] predict = {d -> true, null};
        IntStream primes = IntStream.iterate(2, d -> d + 1)
                .filter(d -> predict[0].test(d))
                .peek(d -> predict[0] = predict[0].and(d2 -> d2 % d != 0));
        return primes;
    }

    /**
     * The accumulating predict func may cause StackOverFlow error.
     * @return
     */
    public static int prime(int index) {
        if (index < 0) throw new IllegalArgumentException();
        IntPredicate[] predict = {d -> true, null};
        int ithPrime = IntStream.iterate(2, d -> d + 1)
                .filter(d -> predict[0].test(d))
                .peek(d -> predict[0] = predict[0].and(d2 -> d2 % d != 0))
                .skip(index)
                .findFirst().getAsInt();
//        PrimitiveIterator.OfInt iterator = primes().iterator();
//        for (int i = 0; i < index; ++i) iterator.nextInt();
//        return iterator.nextInt();
        return ithPrime;
    }

    public static int[] randomInts(int W, int from, int to) {
        assert from < to;
        int[] res = new int[W];
        for (int i = 0; i < W; ++i) res[i] = RandomUtils.uniform(from, to);
        return res;
    }

    public static long[][] chooseTable(int n) {
        long[][] C = new long[n + 1][n + 1];
        for (int i = 0; i <= n; ++i) C[i][0] = 1;
        for (int i = 1; i <= n; ++i)
            for (int j = 1; j <= n; ++j) C[i][j] = (C[i - 1][j] + C[i - 1][j - 1]) % modulus;
        return C;
    }

    public static int reverse(int n) {
        if (n < 0) throw new IllegalArgumentException();
        int ans = 0;
        while (true) {
            if (n == 0) break;
            ans = ans * 10 + n % 10;
            n /= 10;
        }
        return ans;
    }

    public static int[] toArray(long n) {
        String nstr = String.valueOf(n);
        int[] res = new int[nstr.length()];
        for (int i = 0; i < res.length; ++i) res[i] = nstr.charAt(i) - '0';
        return res;
    }

    public static long fromArray(int[] arr) {
        long res = 0;
        for (int i = 0; i < arr.length; ++i) res = res * 10 + arr[i];
        return res;
    }


    public static String toRoman(long n) {
        TreeMap<Integer, String> romanMap = new TreeMap<>();
        romanMap.put(1, "I");
        romanMap.put(5, "V");
        romanMap.put(10, "X");
        romanMap.put(50, "L");
        romanMap.put(100, "C");
        romanMap.put(500, "D");
        romanMap.put(1000, "M");
        romanMap.put(4, "IV");
        romanMap.put(9, "IX");
        romanMap.put(40, "XL");
        romanMap.put(90, "XC");
        romanMap.put(400, "CD");
        romanMap.put(900, "CM");
        StringBuilder res = new StringBuilder();
        long tmpn = n;
        for (int factor : romanMap.descendingKeySet()) {
            while (true) {
                if (factor > tmpn) break;
                res.append(romanMap.get(factor));
                tmpn -= factor;
            }
        }
        return res.toString();
    }

    public static long fromRoman(String roman) {
        TreeMap<String, Integer> romanMap = new TreeMap<>();
        romanMap.put("I", 1);
        romanMap.put("V", 5);
        romanMap.put("X", 10);
        romanMap.put("L", 50);
        romanMap.put("C", 100);
        romanMap.put("D", 500);
        romanMap.put("M", 1000);
        romanMap.put("IV", 4);
        romanMap.put("IX", 9);
        romanMap.put("XL", 40);
        romanMap.put("XC", 90);
        romanMap.put("CD", 400);
        romanMap.put("CM", 900);
        long res = 0;
        IntArrayList factors = new IntArrayList();
        for (int i = 0; i < roman.length(); ) {
            if (i + 1 < roman.length()) {
                String minus = roman.substring(i, i + 2);
                if (romanMap.containsKey(minus)) {
                    factors.add(romanMap.get(minus));
                    i += 2;
                    continue;
                }
            }
            String cur = roman.substring(i, i + 1);
            if (!romanMap.containsKey(cur)) throw new IllegalArgumentException();
            factors.add(romanMap.get(cur));
            i++;
        }

        boolean decending = true;
        for (int i = 0; i < factors.size() - 1; ++i) if (factors.get(i) < factors.get(i + 1)) {decending = false; break;}
        if (!decending) throw new IllegalArgumentException();
        return factors.stream().sum();
    }

    private static boolean isValidRoman(String roman) {
        return true;
    }

    public static void main(String[] args) {
        testPrime();
    }

    public static void testPrime() {
        System.out.println(IntStream.iterate(2, d -> d + 1).filter(d -> d % 2 == 0).skip(100).findFirst().getAsInt());
        System.out.println(prime(100000));
        System.out.println(Arrays.toString(primes().limit(100).toArray()));
    }

    public static void testRoman() {
        while (true) {
            int n = RandomUtils.uniform(1000);
            String roman = toRoman(n);
            long rn = fromRoman(roman);
            System.out.println(n + " -> " + roman);
            System.out.println(roman + " -> " + rn);
            if (rn != n) {
                System.out.println(n + " -> " + roman);
                System.out.println(roman + " -> " + rn);
                throw new RuntimeException();
            }
        }
    }

    public static void testLinearCongruence() {
        long[] A = new long[]{3};
        long[] B = new long[]{0};
        long[] Mod = new long[]{100};
        System.out.println(linearCongruence(A, B, Mod));
    }

    public static List<Integer> factors(int d) {
        int dd = d;
        List<Integer> ans = new ArrayList<>();
        for (int i = 2; i <= dd; ++i) if (dd % i == 0) {
            ans.add(i);
            while (dd % i == 0) dd /= i;
        }
        return ans;
    }
}
