package template.collection.intervals;

/**
 * @author dy[jealousing@gmail.com] on 17-3-14.
 */
public abstract class IntAbstractIntervalTree<RESULT_TYPE> {
    private int ROOT = 0;
    //private int[] delta; /* range update */

    public IntAbstractIntervalTree() {
    }

    protected abstract int arraySize();

    protected abstract int joinDelta(int delta1, int delta2);

    protected abstract int getDelta(int root);

    protected abstract int baseDelta();

    protected abstract void setDelta(int root, int newDelta);

    protected abstract void initLeaf(int root, int arrayIndex);

    protected abstract void initAfter(int root, int left, int right);

    protected abstract void updateFull(int root, int from, int to, int left, int right, int updateDelta);

    protected abstract void updateAfter(int root, int from, int to, int left, int right, int curDelta);

    protected abstract RESULT_TYPE queryFull(int root, int from, int to, int left, int right, int deltaAcc);

    protected abstract RESULT_TYPE queryAfter(RESULT_TYPE leftResult, RESULT_TYPE rightResult);


    protected int nodeCount(int arraySize) { return (Integer.highestOneBit(arraySize) << 2) + ROOT;}

    protected void init() {

        init(ROOT, 0, arraySize());
    }

    private void init(int root, int from, int to) {
        if (from >= to) return;
        if (from + 1 == to) {
            setDelta(root, baseDelta());
            initLeaf(root, from);
            return;
        }
        int mid = from + (to - from) / 2;
        init(root * 2 + 1, from, mid);
        init(root * 2 + 2, mid, to);
        setDelta(root, baseDelta());
        initAfter(root, root * 2 + 1, root * 2 + 2);
    }

    public void update(int updateFrom, int updateTo, int delta) {
        check(0, arraySize(), updateFrom, updateTo);
        update(ROOT, 0, arraySize(), updateFrom, updateTo, delta);
    }

    private void update(int root, int from, int to, int updateFrom, int updateTo, int delta) {
        if (updateFrom <= from && to <= updateTo) {
            updateFull(root, from, to, root * 2 + 1, root * 2 + 2, delta);
            setDelta(root, joinDelta(delta, getDelta(root)));
            return;
        }
        int mid = from + (to - from) / 2;
        if (!(updateTo <= from || mid <= updateFrom)) update(root * 2 + 1, from, mid, updateFrom, updateTo, delta);
        if (!(updateTo <= mid || to <= updateFrom)) update(root * 2 + 2, mid, to, updateFrom, updateTo, delta);
        updateAfter(root, from, to, root * 2 + 1, root * 2 + 2, getDelta(root));
    }

    private RESULT_TYPE query(int root, int from, int to, int queryFrom, int queryTo, int deltaAcc) {
        if (queryFrom <= from && to <= queryTo) {
            return queryFull(root, from, to, root * 2 + 1, root * 2 + 2, deltaAcc);
        }
        int mid = from + (to - from) / 2;
        if (queryFrom < mid && mid < queryTo) {
            RESULT_TYPE leftResult = query(root * 2 + 1, from, mid, queryFrom, queryTo, joinDelta(deltaAcc, getDelta(root)));
            RESULT_TYPE rightResult = query(root * 2 + 2, mid, to, queryFrom, queryTo, joinDelta(deltaAcc, getDelta(root)));
            return queryAfter(leftResult, rightResult);
        }
        if (from < queryTo && queryTo <= mid) {
            RESULT_TYPE leftResult = query(root * 2 + 1, from, mid, queryFrom, queryTo, joinDelta(deltaAcc, getDelta(root)));
            return leftResult;
        }
        if (mid <= queryFrom && queryFrom < to) {
            RESULT_TYPE rightResult = query(root * 2 + 2, mid, to, queryFrom, queryTo, joinDelta(deltaAcc, getDelta(root)));
            return rightResult;
        }

        throw new RuntimeException();
    }

    public RESULT_TYPE query(int queryFrom, int queryTo) {
        check(0, arraySize(), queryFrom, queryTo);
        return query(ROOT, 0, arraySize(), queryFrom, queryTo, baseDelta());
    }

    private void check(int from, int to, int rangeFrom, int rangeTo) {
        if (from >= to || rangeFrom >= rangeTo) throw new IllegalArgumentException();
        if (!(from <= rangeFrom && rangeTo <= to)) throw new IllegalArgumentException();
    }

    private void check(int from, int to) {
        if (from >= to) throw new IllegalArgumentException();
    }
}
