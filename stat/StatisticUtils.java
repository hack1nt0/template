package template.stat;

/**
 * Created by dy on 16-11-27.
 */
public class StatisticUtils {
    public static double cdfNormal(double x) {
        double res = x;
        double acc = x;
        for (int i = 1; i <= 100; ++i) {
            acc = acc * x * x / (2 * i + 1);
            res += acc;
        }
        res = 0.5 + res / Math.sqrt(Math.PI * 2) * Math.exp(-x * x / 2);
        return res;
    }


    public static double var(double[] xs) {
        double mean = mean(xs);
        double variance = 0;
        for (double x : xs) variance += (x - mean) * (x - mean);
        return variance / xs.length;
    }

    public static double mean(double[] xs) {
        double sum = 0;
        for (double x : xs) sum += x;
        return sum / xs.length;
    }
}
