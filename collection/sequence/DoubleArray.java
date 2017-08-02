//package template.collection.sequence;
//
///**
// * @author dy[jealousing@gmail.com] on 17-4-29.
// */
//public interface DoubleArray {
//    int length();
//    double get(int i);
//
//    static DoubleArray from(double[] xs, int from, int to) {
//        return new DoubleArray() {
//            @Override
//            public int length() {
//                return to - from;
//            }
//
//            @Override
//            public double get(int i) {
//                return xs[i + from];
//            }
//        };
//    }
//
//    static DoubleArray from(double[] xs) { return from(xs, 0, xs.length); }
//
//    default DoubleArray subArray(int from, int to) {
//        return subArray(this, from, to);
//    }
//
//    default DoubleArray subArray(DoubleArray parent, int from, int to) {
//        return new DoubleArray() {
//            @Override
//            public int length() {
//                return to - from;
//            }
//
//            @Override
//            public double get(int i) {
//                return parent.get(i + from);
//            }
//        };
//    }
//
//}
