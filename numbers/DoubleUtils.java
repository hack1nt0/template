package template.numbers;

import template.geometry.GeometryUtils;

import java.util.Comparator;

/**
 * Created by dy on 17-1-19.
 */
public class DoubleUtils {

    public static int compare(double a, double b, double epsilon) {
        if (Math.abs(a - b) <= epsilon) return 0;
        return a > b ? +1 : -1;
    }

    public static int compare(double a, double b) {
        // TODO: 17-1-19  NaN ? Inf?
        if (Math.abs(a - b) <= GeometryUtils.epsilon) return 0;
        return a > b ? +1 : -1;
    }
}
