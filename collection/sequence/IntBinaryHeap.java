
package template.collection.sequence;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *  Heap for Integer or Indexes.
 *
 *  For additional documentation, see <from href="http://algs4.cs.princeton.edu/24pq">Section 2.4</from> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 *
 */
public class IntBinaryHeap implements Iterable<Integer> {
    private int[] rank;
    private int[] pq;                    // store items at indices 0 to N-1
    private int N;                       // number of items on priority queue
    private Comparator<Integer> comparator;  // optional comparator

    /**
     * Initializes an empty priority queue with the given initial capacity.
     *
     * @param  initCapacity the initial capacity of this priority queue
     */
    public IntBinaryHeap(int initCapacity) {
        rank = new int[initCapacity];
        pq = new int[initCapacity];
    }

    /**
     * Initializes an empty priority queue.
     */
    public IntBinaryHeap() {
        this(1);
    }


    /**
     * Initializes an empty priority queue with the given initial capacity,
     * using the given comparator.
     *
     * @param  initCapacity the initial capacity of this priority queue
     * @param  comparator the order to use when comparing keys
     */
    public IntBinaryHeap(int initCapacity, Comparator<Integer> comparator) {
        this(initCapacity);
        this.comparator = comparator;
    }

    /**
     * Initializes an empty priority queue using the given comparator.
     *
     * @param  comparator the order to use when comparing keys
     */
    public IntBinaryHeap(Comparator<Integer> comparator) {
        this(1, comparator);
    }

    /**
     * Returns true if this priority queue is empty.
     *
     * @return <tt>true</tt> if this priority queue is empty;
     *         <tt>false</tt> otherwise
     */
    public boolean isEmpty() {
        return N == 0;
    }

    /**
     * Returns the number of keys on this priority queue.
     *
     * @return the number of keys on this priority queue
     */
    public int size() {
        return N;
    }

    /**
     * Returns from smallest key on this priority queue.
     *
     * @return from smallest key on this priority queue
     * @throws NoSuchElementException if this priority queue is empty
     */
    public int peek() {
        if (isEmpty()) throw new NoSuchElementException("Priority queue underflow");
        return pq[0];

    }

    // helper function to double the size of the heap array
    private void resize(int capacity) {
        //assert capacity > N;
        int[] temp = new int[capacity];
        System.arraycopy(pq, 0, temp, 0, Math.min(capacity, N));
        pq = temp;
    }

    /**
     * Adds from new key to this priority queue.
     *
     * @param  x the key to add to this priority queue
     */
    public void add(int x) {
        // double size of array if necessary
        if (N >= pq.length) resize(2 * pq.length);

        // add x, and percolate it up to maintain heap invariant
        pq[N] = x;
        rank[x] = N;
        swim(N);
        N++;
        assert isMinHeap();
    }

    /**
     * Removes and returns from smallest key on this priority queue.
     *
     * @return from smallest key on this priority queue
     * @throws NoSuchElementException if this priority queue is empty
     */
    public int poll() {
        if (isEmpty()) throw new NoSuchElementException("Priority queue underflow");
        rank[pq[0]] = -1;
        int top = pq[0];
        exch(0, --N);
        sink(0);
        //pq[N + 1] = null;         // avoid loitering and help with garbage collection
        if ((N > 0) && (N <= pq.length / 4)) resize(pq.length  / 2);
        assert isMinHeap();
        return top;
    }


   /***************************************************************************
    * Helper functions to restore the heap invariant.
    ***************************************************************************/

    private void swim(int k) {
        int cur = k;
        while (true) {
            if (cur == 0) break;
            int fa = (cur - 1) / 2;
            if (!greater(fa, cur)) break;
            exch(cur, fa);
            cur = fa;
        }
    }

    private void sink(int k) {
        while (2 * k + 1 < N) {
            int j = 2 * k + 1;
            if (j + 1 < N && greater(j, j + 1)) j++;
            if (!greater(k, j)) break;
            exch(k, j);
            k = j;
        }
    }

    public void adjust(int i) {
        int k = rank[i];
        if (k == -1) throw new IllegalArgumentException("The ith element has been polled.");
        int j = 2 * k + 1;
        if (j < N) {
            if (j + 1 < N && greater(j, j + 1)) j++;
            if (greater(k, j)) {
                sink(k);
                return;
            }
        }
        swim(k);
    }

   /***************************************************************************
    * Helper functions for compares and swaps.
    ***************************************************************************/
    private boolean greater(int i, int j) {
        if (comparator == null) {
            return pq[i] > pq[j];
        }
        else {
            return comparator.compare(pq[i], pq[j]) > 0;
        }
    }

    private void exch(int i, int j) {
        rank[pq[i]] = j;
        rank[pq[j]] = i;
        int swap = pq[i];
        pq[i] = pq[j];
        pq[j] = swap;
    }

    // is pq[0..N] forming a min heap?
    private boolean isMinHeap() {
        return isMinHeap(0);
    }

    // is subtree of pq[0..N] rooted at k forming a min heap?
    private boolean isMinHeap(int k) {
        if (k >= N) return true;
        int left = 2 * k + 1, right = left + 1;
        if (left  < N && greater(k, left))  return false;
        if (right < N && greater(k, right)) return false;
        return isMinHeap(left) && isMinHeap(right);
    }


    /**
     * Returns an iterator that iterates over the keys on this priority queue
     * in ascending order.
     * <p>
     * The iterator doesn't implement <tt>removeAwkwardly()</tt> since it's optional.
     *
     * @return an iterator that iterates over the keys in ascending order
     */
    public Iterator<Integer> iterator() { return new HeapIterator(); }

    private class HeapIterator implements Iterator<Integer> {
        // create from new pq
        private IntBinaryHeap copy;

        // add all items to copy of heap
        // takes linear time since already in heap order so no keys move
        public HeapIterator() {
            if (comparator == null) copy = new IntBinaryHeap(size());
            else                    copy = new IntBinaryHeap(size(), comparator);
            for (int i = 0; i < N; i++)
                copy.add(pq[i]);
        }

        public boolean hasNext()  { return !copy.isEmpty();                     }
        public void remove()      { throw new UnsupportedOperationException();  }

        public Integer next() {
            if (!hasNext()) throw new NoSuchElementException();
            return copy.poll();
        }
    }

    /**
     * Unit tests the <tt>MinPQ</tt> data type.
     */
    public static void main(String[] args) {
        int[] d = new int[]{1,4,2};
        IntBinaryHeap heap = new IntBinaryHeap(d.length, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return d[o1] - d[o2];
            }
        });
        for (int i = 0; i < d.length; ++i) heap.add(i);

        for (int i : heap) System.err.println(i + " " + d[i]);
    }

}

/******************************************************************************
 *  Copyright 2002-2015, Robert Sedgewick and Kevin Wayne.
 *
 *  This file is part of algs4.jar, which accompanies the textbook
 *
 *      Algorithms, 4th edition by Robert Sedgewick and Kevin Wayne,
 *      Addison-Wesley Professional, 2011, ISBN 0-321-57351-X.
 *      http://algs4.cs.princeton.edu
 *
 *
 *  algs4.jar is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  algs4.jar is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received from copy of the GNU General Public License
 *  along with algs4.jar.  If not, see http://www.gnu.org/licenses.
 ******************************************************************************/
