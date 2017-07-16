package template.collection.intervals;

import template.debug.RandomUtils;
import template.debug.Stopwatch;
import template.numbers.IntUtils;

import java.util.Arrays;

/**
 * @author dy[jealousing@gmail.com] on 17-3-14.
 */
public class IntMinIntervalTree extends IntAbstractIntervalTree<Integer> {
    int[] array;
    int[] min;
    int[] delta;
    int nodeCount;

    public IntMinIntervalTree(int[] array) {
        this.array = array;
        this.nodeCount = nodeCount(array.length);
        min = new int[nodeCount];
        delta = new int[nodeCount];
        init();
    }

    @Override
    public int arraySize() {
        return array.length;
    }

    @Override
    public void initLeaf(int root, int arrayIndex) {
        min[root] = array[arrayIndex];
    }

    @Override
    public void initAfter(int root, int left, int right) {
        min[root] = Math.min(min[left], min[right]);
    }

    @Override
    public void updateFull(int root, int from, int to, int left, int right, int updateDelta) {
        min[root] += updateDelta;
    }

    @Override
    public void updateAfter(int root, int from, int to, int left, int right, int curDelta) {
        min[root] = Math.min(min[left], min[right]) + curDelta;
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
    public Integer queryFull(int root, int from, int to, int left, int right, int deltaAcc) {
        return min[root] + deltaAcc;
    }

    @Override
    public Integer queryAfter(Integer leftResult, Integer rightResult) {
        return Math.min(leftResult, rightResult);
    }

    public static void main(String[] args) {
        int size = 20000000;
        int[] array = IntUtils.randomInts(size, 0, size);
        IntMinIntervalTree intMinIntervalTree = new IntMinIntervalTree(array);
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
            intMinIntervalTree.update(updateFrom, updateTo, delta);
            int min2 = intMinIntervalTree.query(queryFrom, queryTo);
            Stopwatch.toc();

            Stopwatch.tic();
            int min1 = Integer.MAX_VALUE;
            for (int i = queryFrom; i < queryTo; ++i) {
                min1 = Math.min(min1, updateFrom <= i && i < updateTo ? array[i] + delta : array[i]);
            }
            Stopwatch.toc();

            if (min1 != min2) {
                System.out.println(min1 + " " + min2);
                intMinIntervalTree.update(updateFrom, updateTo, delta);
                intMinIntervalTree.query(queryFrom, queryTo);
                throw new RuntimeException();
            }

            for (int i = updateFrom; i < updateTo; ++i) array[i] += delta;

            System.out.println(min1);
        }
    }

}
