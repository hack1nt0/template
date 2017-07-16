package template.numbers;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dy on 16-10-8.
 */
public class Rational implements Comparable<Rational> {
        long numerator, denominator;

        public Rational(long numerator) {
            this.numerator = numerator;
            this.denominator = 1;
        }

        public Rational(long numerator, long denominator) {
            if (denominator == 0) throw new IllegalArgumentException();
            long d = IntUtils.gcd(numerator, denominator);
            this.numerator = numerator / d;
            this.denominator = denominator / d;
        }

        public Rational add(Rational that) {
            //if (numerator == 0 && to.numerator == 0) return new Real(0, 1);
            long x = this.numerator * that.denominator + that.numerator * this.denominator;
            long y = this.denominator * that.denominator;
            return new Rational(x, y);
        }

        @Override
        public String toString() {
            if (denominator < 0) {
                numerator = -numerator;
                denominator = -denominator;
            }
            return numerator + "/" + denominator;
        }

        public String toDecimal() {
            Map<Long, Integer> usedDividends = new HashMap<>();
            StringBuilder decimal = new StringBuilder();
            long divisor = denominator;
            long dividend = numerator;
            decimal.append(dividend / divisor).append('.');
            dividend = dividend % divisor * 10;
            if (dividend == 0) {
                decimal.append('0');
                return decimal.toString();
            }
            while (true) {
                if (usedDividends.containsKey(dividend)) {
                    int pos = usedDividends.get(dividend);
                    decimal.insert(pos, '(');
                    decimal.append(')');
                    break;
                }
                if (dividend == 0) break;
                usedDividends.put(dividend, decimal.length());
                decimal.append(dividend / divisor);
                dividend = dividend % divisor * 10;
            }

            return decimal.toString();
        }

    @Override
    public int compareTo(Rational that) {
            long cmp = this.numerator * that.denominator - that.numerator * this.denominator;
            if (cmp > 0) return +1;
            if (cmp < 0) return -1;
            return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Rational rational = (Rational) o;

        if (numerator != rational.numerator) return false;
        return denominator == rational.denominator;
    }

    @Override
    public int hashCode() {
        int result = (int) (numerator ^ (numerator >>> 32));
        result = 31 * result + (int) (denominator ^ (denominator >>> 32));
        return result;
    }
}