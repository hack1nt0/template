package template.geometry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dy on 17-1-15.
 */
public class Rectangle {
    private final Point lowerLeft, upperRight;
    private Point upperLeft, lowerRight;

    public Rectangle(Point lowerLeft, Point upperRight) {
        if (!(lowerLeft.x < upperRight.x && lowerLeft.y < upperRight.y)) throw new IllegalArgumentException();
        this.lowerLeft = lowerLeft;
        this.upperRight = upperRight;
    }

    public Point getLowerLeft() {
        return lowerLeft;
    }

    public Point getUpperRight() {
        return upperRight;
    }

    public Point getUpperLeft() {
        if (upperLeft == null) upperLeft = new Point(lowerLeft.x, upperRight.y);
        return upperLeft;
    }

    public Point getLowerRight() {
        if (lowerRight == null) lowerRight = new Point(upperRight.x, lowerLeft.y);
        return lowerRight;
    }

    public boolean overlapWith(Rectangle that) {
        boolean xo = !(upperRight.x <= that.lowerLeft.x || that.upperRight.x <= lowerLeft.x);
        boolean yo = !(upperRight.y <= that.lowerLeft.y || that.upperRight.y <= lowerLeft.y);
        return xo && yo;
    }

    public Rectangle overlap(Rectangle that) {
        if (!overlapWith(that)) throw new IllegalArgumentException();
        double x1 = Math.max(lowerLeft.x, that.lowerLeft.x);
        double x2 = Math.min(upperRight.x, that.upperRight.x);
        double y1 = Math.max(lowerLeft.y, that.lowerLeft.y);
        double y2 = Math.min(upperRight.y, that.upperRight.y);
        return new Rectangle(new Point(x1, y1), new Point(x2, y2));
    }

    public boolean cover(Rectangle that) {
        boolean xc = lowerLeft.x <= that.lowerLeft.x && that.upperRight.x <= upperRight.x;
        boolean yc = lowerLeft.y <= that.lowerLeft.y && that.upperRight.y <= upperRight.y;
        return xc && yc;
    }

    public List<Rectangle> add(Rectangle that) {
        List<Rectangle> res = new ArrayList<>();
        if (!overlapWith(that)) {
            res.add(this);
            res.add(that);
            return res;
        }
        res.add(this);
        res.addAll(that.remove(this));
        return res;
    }

    public List<Rectangle> remove(Rectangle o) {
        List<Rectangle> res = new ArrayList<>();
        if (!overlapWith(o)) {
            res.add(this);
            return res;
        }
        double x1 = getLowerLeft().x;
        double y1 = getLowerLeft().y;
        double x2 = getUpperRight().x;
        double y2 = getUpperRight().y;
        if (x1 < o.getLowerLeft().x) {
            res.add(new Rectangle(getLowerLeft(), new Point(o.getLowerLeft().x, y2)));
            x1 = o.getLowerLeft().x;
        }
        if (o.getLowerRight().x < x2) {
            res.add(new Rectangle(new Point(o.getLowerRight().x, y1), getUpperRight()));
            x2 = o.getLowerRight().x;
        }
        if (y1 < o.getLowerLeft().y) {
            res.add(new Rectangle(new Point(x1, y1), new Point(x2, o.getLowerLeft().y)));
            y1 = o.getLowerLeft().y;
        }
        if (o.getUpperLeft().y < y2) {
            res.add(new Rectangle(new Point(x1, o.getUpperLeft().y), new Point(x2, y2)));
            y2 = o.getUpperLeft().y;
        }
        return res;
    }

    public List<Rectangle> removeAwkwardly(Rectangle that) {
        List<Rectangle> res = new ArrayList<>();
        if (that.cover(this)) return res;
        if (!overlapWith(that)) {
            res.add(this);
            return res;
        }
        Rectangle removed = overlap(that);
        if (removed.getUpperLeft().equals(getUpperLeft()) && between(removed.getUpperRight(), getUpperLeft(), getUpperRight()) && between(removed.getLowerLeft(), getUpperLeft(), getLowerLeft())) {
            res.add(new Rectangle(removed.getLowerRight(), getUpperRight()));
            res.add(new Rectangle(getLowerLeft(), new Point(getLowerRight().x, removed.getLowerLeft().y)));
            return res;
        }
        if (removed.getUpperLeft().equals(getUpperLeft()) && removed.getUpperRight().equals(getUpperRight()) && between(removed.getLowerLeft(), getUpperLeft(), getLowerLeft()) && between(removed.getLowerRight(), getUpperRight(), getLowerRight())) {
            res.add(new Rectangle(getLowerLeft(), removed.getLowerRight()));
            return res;
        }
        if (removed.getUpperLeft().equals(getUpperLeft()) && removed.getLowerLeft().equals(getLowerLeft()) && between(removed.getUpperRight(), getUpperLeft(), getUpperRight()) && between(removed.getLowerRight(), getLowerLeft(), getLowerRight())) {
            res.add(new Rectangle(removed.getLowerRight(), getUpperRight()));
            return res;
        }
        if (between(removed.getUpperLeft(), getUpperLeft(), getUpperRight()) && between(removed.getUpperRight(), getUpperLeft(), getUpperRight()) && this.contains(removed.getLowerLeft())) {
            res.add(new Rectangle(getLowerLeft(), removed.getUpperLeft()));
            res.add(new Rectangle(new Point(removed.getLowerLeft().x, getLowerLeft().y), removed.getLowerRight()));
            res.add(new Rectangle(new Point(removed.getLowerRight().x, getLowerLeft().y), getUpperRight()));
            return res;
        }
        if (between(removed.getUpperLeft(), getUpperLeft(), getUpperRight()) && removed.getUpperRight().equals(getUpperRight()) && this.contains(removed.getLowerLeft())) {
            res.add(new Rectangle(getLowerLeft(), removed.getUpperLeft()));
            res.add(new Rectangle(new Point(removed.getLowerLeft().x, getLowerLeft().y), removed.getLowerRight()));
            return res;
        }
        if (between(removed.getUpperLeft(), getUpperLeft(), getUpperRight()) && between(removed.getUpperRight(), getUpperLeft(), getUpperRight()) && between(removed.getLowerLeft(), getLowerLeft(), getLowerRight())) {
            res.add(new Rectangle(getLowerLeft(), removed.getUpperLeft()));
            res.add(new Rectangle(removed.getLowerRight(), getUpperRight()));
            return res;
        }
        if (between(removed.getUpperLeft(), getUpperLeft(), getUpperRight()) && removed.getUpperRight().equals(getUpperRight()) && removed.getLowerRight().equals(getLowerRight())) {
            res.add(new Rectangle(getLowerLeft(), removed.getUpperLeft()));
            return res;
        }

        if (between(removed.getUpperLeft(), getUpperLeft(), getLowerLeft()) && between(removed.getLowerLeft(), getUpperLeft(), getLowerLeft()) && this.contains(removed.getUpperRight())) {
            res.add(new Rectangle(removed.getUpperLeft(), getUpperRight()));
            res.add(new Rectangle(removed.getLowerRight(), new Point(getUpperRight().x, removed.getUpperRight().y)));
            res.add(new Rectangle(getLowerLeft(), new Point(getUpperRight().x, removed.getLowerRight().y)));
            return res;
        }
        if (between(removed.getUpperLeft(), getUpperLeft(), getLowerLeft()) && between(removed.getLowerLeft(), getUpperLeft(), getLowerLeft()) && between(removed.getUpperRight(), getLowerRight(), getUpperRight())) {
            res.add(new Rectangle(removed.getUpperLeft(), getUpperRight()));
            res.add(new Rectangle(getLowerLeft(), removed.getLowerRight()));
            return res;
        }
        if (this.contains(removed.getUpperLeft()) && this.contains(removed.getLowerLeft()) && this.contains(removed.getUpperRight())) {
            res.add(new Rectangle(getLowerLeft(), new Point(removed.getUpperLeft().x, getUpperRight().y)));
            res.add(new Rectangle(removed.getUpperLeft(), new Point(removed.getUpperRight().x, getUpperRight().y)));
            res.add(new Rectangle(new Point(removed.getLowerLeft().x, getLowerLeft().y), removed.getLowerRight()));
            res.add(new Rectangle(new Point(removed.getLowerRight().x, getLowerRight().y), getUpperRight()));
            return res;
        }
        if (this.contains(removed.getUpperLeft()) && this.contains(removed.getLowerLeft()) && between(removed.getUpperRight(), getLowerRight(), getUpperRight())) {
            res.add(new Rectangle(getLowerLeft(), new Point(removed.getUpperLeft().x, getUpperRight().y)));
            res.add(new Rectangle(removed.getUpperLeft(), getUpperRight()));
            res.add(new Rectangle(new Point(removed.getLowerLeft().x, getLowerLeft().y), removed.getLowerRight()));
            return res;
        }

        if (between(removed.getUpperLeft(), getLowerLeft(), getUpperLeft()) && removed.getLowerLeft().equals(getLowerLeft()) && this.contains(removed.getUpperRight())) {
            res.add(new Rectangle(removed.getUpperLeft(), getUpperRight()));
            res.add(new Rectangle(removed.getLowerRight(), new Point(getLowerRight().x, removed.getUpperRight().y)));
            return res;
        }
        if (between(removed.getUpperLeft(), getLowerLeft(), getUpperLeft()) && removed.getLowerLeft().equals(getLowerLeft()) && removed.getLowerRight().equals(getLowerRight())) {
            res.add(new Rectangle(removed.getUpperLeft(), getUpperRight()));
            return res;
        }
        if (this.contains(removed.getUpperLeft()) && this.contains(removed.getUpperRight()) && between(removed.getLowerLeft(), getLowerLeft(), getLowerRight())) {
            res.add(new Rectangle(getLowerLeft(), new Point(removed.getLowerLeft().x, getUpperRight().y)));
            res.add(new Rectangle(removed.getUpperLeft(), new Point(removed.getUpperRight().x, getUpperRight().y)));
            res.add(new Rectangle(removed.getLowerRight(), getUpperRight()));
            return res;
        }
        if (this.contains(removed.getUpperLeft()) && between(removed.getLowerLeft(), getLowerLeft(), getLowerRight()) && removed.getLowerRight().equals(getLowerRight())) {
            res.add(new Rectangle(getLowerLeft(), new Point(removed.getLowerLeft().x, getUpperRight().y)));
            res.add(new Rectangle(removed.getUpperLeft(), getUpperRight()));
            return res;
        }
        throw new RuntimeException();
    }

    public double area() {
        return (upperRight.x - lowerLeft.x) * (upperRight.y - lowerLeft.y);
    }

    public boolean between(Point a, Point b, Point c) {
        if (b.equals(c)) throw new IllegalArgumentException();
        if (b.y == c.y && c.x < b.x) {
            Point swap = b;
            b = c;
            c = swap;
        }
        if (b.x == c.x && c.y > b.y) {
            Point swap = b;
            b = c;
            c = swap;
        }
        return  b.y == a.y && a.y == c.y && b.x < a.x && a.x < c.x ||
                b.x == a.x && a.x == c.x && c.y < a.y && a.y < b.y;
    }

    public boolean contains(Point a, boolean strict) {
        if (strict) return lowerLeft.x < a.x && a.x < upperRight.x && lowerLeft.y < a.y && a.y < upperRight.y;
        return lowerLeft.x <= a.x && a.x <= upperRight.x && lowerLeft.y <= a.y && a.y <= upperRight.y;
    }

    public boolean contains(Point a) {
        return contains(a, true);
    }

    public double perimeter() {
        return (getLowerRight().x - getLowerLeft().x + getUpperLeft().y - getLowerLeft().y) * 2;
    }
}

