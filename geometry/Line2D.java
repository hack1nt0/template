package template.geometry;

/**
 * @author Egor Kulikov (kulikov@devexperts.com)
 */
public class Line2D {
    public final double a;
    public final double b;
    public final double c;

    public Line2D(Point p, double angle) {
        a = Math.sin(angle);
        b = -Math.cos(angle);
        c = -p.x * a - p.y * b;
    }

    public Line2D(double a, double b, double c) {
        double h = GeometryUtils.fastHypot(a, b);
        this.a = a / h;
        this.b = b / h;
        this.c = c / h;
    }

    /**
     *  y - y0    x - x0
     * ------- = -------
     * y1 - y0   x1 - x0
     * @param p0
     * @param p1
     */
    public Line2D(Point p0, Point p1) {
        /*
        double a = p1.y - p0.y;
        double b = p0.x - p1.x;
        double c = (p1.x - p0.x) * p0.y - (p1.y - p0.y) * p0.x;
        */
        this(p1.y - p0.y, p0.x - p1.x, (p1.x - p0.x) * p0.y - (p1.y - p0.y) * p0.x);
    }

    public double y(double x) {
        return -(a * x + c) / b;
    }

    public double x(double y) {
        return -(b * y + c) / a;
    }

    public Point intersect(Line2D other) {
        if (parallel(other)) {
            return null;
        }
        double determinant = b * other.a - a * other.b;
        double x = (c * other.b - b * other.c) / determinant;
        double y = (a * other.c - c * other.a) / determinant;
        return new Point(x, y);
    }

    public boolean parallel(Line2D other) {
        return Math.abs(a * other.b - b * other.a) < GeometryUtils.epsilon;
    }

    public boolean contains(Point point) {
        return Math.abs(value(point)) < GeometryUtils.epsilon;
    }

    public Line2D perpendicular(Point point) {
        return new Line2D(-b, a, b * point.x - a * point.y);
    }

    public double value(Point point) {
        return a * point.x + b * point.y + c;
    }

    public Point[] intersect(Circle circle) {
        double distance = distance(circle.center);
        if (distance > circle.radius + GeometryUtils.epsilon) {
            return new Point[0];
        }
        Point intersection = intersect(perpendicular(circle.center));
        if (Math.abs(distance - circle.radius) < GeometryUtils.epsilon) {
            return new Point[]{intersection};
        }
        double shift = Math.sqrt(circle.radius * circle.radius - distance * distance);
        return new Point[]{new Point(intersection.x + shift * b, intersection.y - shift * a),
                new Point(intersection.x - shift * b, intersection.y + shift * a)};
    }

    public double distance(Point center) {
        return Math.abs(value(center));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Line2D line = (Line2D) o;

        if (!parallel(line)) {
            return false;
        }
        if (Math.abs(a * line.c - c * line.a) > GeometryUtils.epsilon ||
                Math.abs(b * line.c - c * line.b) > GeometryUtils.epsilon) {
            return false;
        }

        return true;
    }

    public double angle() {
        return Math.atan2(-a, b);
    }
}
