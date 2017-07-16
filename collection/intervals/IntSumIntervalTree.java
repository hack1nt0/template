package template.collection.intervals;

import template.debug.RandomUtils;
import template.debug.Stopwatch;
import template.numbers.IntUtils;

/**
 * @author dy[jealousing@gmail.com] on 17-3-15.
 */
public class IntSumIntervalTree extends IntAbstractIntervalTree<Integer> {
    private int[] array;
    private int nodeCount;
    private int[] delta;
    private int[] sum;

    public IntSumIntervalTree(int array[]) {
        this.array = array;
        this.nodeCount = nodeCount(array.length);
        delta = new int[nodeCount];
        sum = new int[nodeCount];
        init();
    }

    @Override
    public int arraySize() {
        return array.length;
    }

    @Override
    public int joinDelta(int delta1, int delta2) {
        return delta1 + delta2;
    }

    @Override
    public int getDelta(int root) {
        return delta[root];
    }

    @Override
    public int baseDelta() {
        return 0;
    }

    @Override
    public void setDelta(int root, int newDelta) {
        delta[root] = newDelta;
    }

    @Override
    public void initLeaf(int root, int arrayIndex) {
        sum[root] = array[arrayIndex];
    }

    @Override
    public void initAfter(int root, int left, int right) {
        sum[root] = sum[left] + sum[right];
    }

    @Override
    public void updateFull(int root, int from, int to, int left, int right, int updateDelta) {
        sum[root] += (to - from) * updateDelta;
    }

    @Override
    public void updateAfter(int root, int from, int to, int left, int right, int curDelta) {
        sum[root] = sum[left] + sum[right] + (to - from) * curDelta;
    }

    @Override
    public Integer queryFull(int root, int from, int to, int left, int right, int deltaAcc) {
        return sum[root] + (to - from) * deltaAcc;
    }
    @Override
    public Integer queryAfter(Integer leftResult, Integer rightResult) {
        return leftResult + rightResult;
    }

    public static void main(String[] args) {

        int size = 20000000;
        int[] array = IntUtils.randomInts(size, 0, size);
        IntSumIntervalTree intSumIntervalTree = new IntSumIntervalTree(array);
        int queryFrom = 0, queryTo = size;

        while (true) {
            int updateFrom = RandomUtils.uniform(size + 1);
            int updateTo = RandomUtils.uniform(size + 1);
            if (updateFrom == updateTo) continue;
            if (updateFrom > updateTo) {
                int swap = updateFrom;
                updateFrom = updateTo;
                updateTo = swap;
            }
            int delta = RandomUtils.uniform(10);
            System.out.println(updateFrom + " " + updateTo + " " + delta);

            Stopwatch.tic();
            intSumIntervalTree.update(updateFrom, updateTo, delta);
            long sum2 = intSumIntervalTree.query(queryFrom, queryTo);
            Stopwatch.toc();

            Stopwatch.tic();
            long sum1 = 0;
            for (int i = queryFrom; i < queryTo; ++i) {
                sum1 += updateFrom <= i && i < updateTo ? array[i] + delta : array[i];
            }
            Stopwatch.toc();

//            if (sum1 != sum2) {
//                System.out.println(sum1 + " " + sum2);
//                intSumIntervalTree.update(updateFrom, updateTo, delta);
//                intSumIntervalTree.query(queryFrom, queryTo);
//                throw new RuntimeException();
//            }

            for (int i = updateFrom; i < updateTo; ++i) array[i] += delta;

            System.out.println(sum1);
        }
    }
}
