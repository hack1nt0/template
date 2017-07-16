package template.geometry;

/**
 * @author Egor Kulikov (kulikov@devexperts.com)
 */
public class LineSegment {
    public final Point a;
    public final Point b;
    private double distance = Double.NaN;
    private Line2D line = null;

    public LineSegment(Point a, Point b) {
        this.a = a;
        this.b = b;
    }

    public double length() {
        if (Double.isNaN(distance)) {
            distance = a.distance(b);
        }
        return distance;
    }

    public double distance(Point point) {
        double length = length();
        double left = point.distance(a);
        if (length < GeometryUtils.epsilon) {
            return left;
        }
        double right = point.distance(b);
        if (left * left > right * right + length * length) {
            return right;
        }
        if (right * right > left * left + length * length) {
            return left;
        }
        return point.distance(line());
    }

    public Point intersect(LineSegment other, boolean includeEnds) {
        Line2D line = line();
        Line2D otherLine = other.a.line(other.b);
        if (line.parallel(otherLine)) {
            return null;
        }
        Point intersection = line.intersect(otherLine);
        if (contains(intersection, includeEnds) && other.contains(intersection, includeEnds)) {
            return intersection;
        } else {
            return null;
        }
    }

    public boolean contains(Point point, boolean includeEnds) {
        if (a.equals(point) || b.equals(point)) {
            return includeEnds;
        }
        if (a.equals(b)) {
            return false;
        }
        Line2D line = line();
        if (!line.contains(point)) {
            return false;
        }
        Line2D perpendicular = line.perpendicular(a);
        double aValue = perpendicular.value(a);
        double bValue = perpendicular.value(b);
        double pointValue = perpendicular.value(point);
        return aValue < pointValue && pointValue < bValue || bValue < pointValue && pointValue < aValue;
    }

    public Line2D line() {
        if (line == null) {
            line = a.line(b);
        }
        return line;
    }

    public Point middle() {
        return new Point((a.x + b.x) / 2, (a.y + b.y) / 2);
    }

    public Point[] intersect(Circle circle) {
        Point[] result = line().intersect(circle);
        if (result.length == 0) {
            return result;
        }
        if (result.length == 1) {
            if (contains(result[0], true)) {
                return result;
            }
            return new Point[0];
        }
        if (contains(result[0], true)) {
            if (contains(result[1], true)) {
                return result;
            }
            return new Point[]{result[0]};
        }
        if (contains(result[1], true)) {
            return new Point[]{result[1]};
        }
        return new Point[0];
    }

    public Point intersect(Line2D line) {
        Line2D selfLine = line();
        Point intersect = selfLine.intersect(line);
        if (intersect == null) {
            return null;
        }
        if (contains(intersect, true)) {
            return intersect;
        }
        return null;
    }

    public double distance(LineSegment other) {
        Line2D line = line();
        Line2D otherLine = other.line();
        Point p = line == null || otherLine == null ? null : line.intersect(otherLine);
        if (p != null && contains(p, true) && other.contains(p, true)) {
            return 0;
        }
        return Math.min(Math.min(other.distance(a), other.distance(b)), Math.min(distance(other.a), distance(other.b)));
    }
}
