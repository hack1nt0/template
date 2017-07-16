package template.collection;

import template.collection.sequence.ArrayUtils;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.IntConsumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

/**
 * Created by dy on 2017/2/8.
 *
 *  Size-Resizing Array List & Stack & Queue
 */
public class EArrayList<T> extends AbstractList<T> {
    private Object[] arr;
    private int from, to;
    private int N;
    private boolean sizeFixed;

    public EArrayList(int initCapacity, boolean sizeFixed) {
        arr = new Object[initCapacity];
        this.sizeFixed = sizeFixed;
    }

    public EArrayList(int initCapacity) {
        this(initCapacity, false);
    }

    public EArrayList() {
        this(3, false);
    }

    @SuppressWarnings("unchecked")
    public T getFirst() {
        if (isEmpty()) throw new NoSuchElementException("The queue underflow.");
        return (T) arr[from];
    }

    @SuppressWarnings("unchecked")
    public T getLast() {
        if (isEmpty()) throw new NoSuchElementException("The queue underflow.");
        return (T) arr[to - 1];
    }

    @SuppressWarnings("unchecked")
    public T removeFirst() {
        if (isEmpty()) throw new NoSuchElementException("The queue underflow.");
        T first = (T) arr[from++];
        if (from == arr.length) from = 0;
        N--;
        if (!sizeFixed) if (N > 0 && N == arr.length / 4) resize(arr.length / 2);
        return first;
    }

    @SuppressWarnings("unchecked")
    public T removeLast() {
        if (isEmpty()) throw new NoSuchElementException("The queue underflow.");
        to--;
        if (to < 0) to = arr.length - 1;
        T last = (T) arr[to];
        N--;
        if (!sizeFixed) if (N > 0 && N == arr.length / 4) resize(arr.length / 2);
        return last;
    }

    public boolean isEmpty () {
        return N == 0;
    }

    public void addFirst(T o) {
        if (size() == arr.length) {
            if (sizeFixed) throw new RuntimeException("Queue overflow.");
            resize(arr.length * 2);
        }
        from--;
        if (from < 0) from = arr.length - 1;
        arr[from] = o;
        N++;
    }

    public boolean addLast(T o) {
        boolean resized = false;
        if (size() == arr.length) {
            if (sizeFixed) throw new RuntimeException("Queue overflow.");
            resize(arr.length * 2);
            resized = true;
        }
        arr[to] = o;
        to++;
        if (to == arr.length) to = 0;
        N++;
        return resized;
    }

    public int size() {
        return N;
    }

    private void resize(int capacity) {
        //System.err.println(capacity + " " + N + " " + arr.length);
        Object[] temp = new Object[capacity];
        for (int i = 0; i < N; ++i) temp[i] = arr[(from + i) % arr.length];
        arr = temp;
        from = 0;
        to = N;
    }

    public void clear() {
        from = to = 0;
        N = 0;
    }

    public T peek() {
        return getFirst();
    }

    public T poll() {
        return removeFirst();
    }

    public T peekLast() {
        return getLast();
    }

    public T pollLast() {
        return removeLast();
    }

    public boolean add(T o) {
        return addLast(o);
    }


    public void push(T o) {
        addLast(o);
    }

    public T pop() {
        return pollLast();
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            int left = N;
            int i = 0;
            @Override
            public boolean hasNext() {
                return left > 0;
            }

            @Override
            public T next() {
                left--;
                return get(i++);
            }
        };
    }

    public Integer[] toArray(Integer[] a) {
        return (Integer[]) ArrayUtils.inbox(arr);
    }

    public T get(int index) {
        if (index >= size()) throw new ArrayIndexOutOfBoundsException();
        int i = from + index;
        if (i >= arr.length) i -= arr.length;
        return (T) arr[i];
    }

    public T set(int index, int element) {
        if (index >= size()) throw new ArrayIndexOutOfBoundsException();
        int i = from + index;
        if (i >= arr.length) i -= arr.length;
        T old = (T) arr[i];
        arr[i] = element;
        return old;
    }


    //Copied from java.util.Spliterators
    /**
     * A Spliterator.OfInt designed for use by sources that traverse and split
     * elements maintained in an unmodifiable {@code int[]} array.
     */
    static final class IntCyclicArraySpliterator implements Spliterator.OfInt {
        private final int[] array;
        private int from;        // current from, modified on advance/split
        private int size;  // one past last from
        private static final int characteristics = Spliterator.ORDERED | Spliterator.IMMUTABLE | Spliterator.SIZED | Spliterator.SUBSIZED;

        public IntCyclicArraySpliterator(int[] array) {
            this(array, 0, array.length);
        }

        public IntCyclicArraySpliterator(int[] array, int from, int size) {
            this.array = array;
            this.from = from;
            this.size = size;
        }

        @Override
        public OfInt trySplit() {
            if (size == 0) return null;
            OfInt ans = new IntCyclicArraySpliterator(array, from, size >> 1);
            from += (size >> 1);
            if (from >= array.length) from -= array.length;
            return ans;
        }

        @Override
        public void forEachRemaining(IntConsumer action) {
            if (action == null)
                throw new NullPointerException();
            while (size > 0) {
                action.accept(array[from++]);
                if (from >= array.length) from -= array.length;
                size--;
            }
        }

        @Override
        public boolean tryAdvance(IntConsumer action) {
            if (action == null)
                throw new NullPointerException();
            if (size > 0) {
                action.accept(array[from++]);
                if (from >= array.length) from -= array.length;
                size--;
                return true;
            }
            return false;
        }

        @Override
        public long estimateSize() { return (long)size; }

        @Override
        public int characteristics() {
            return characteristics;
        }

        @Override
        public Comparator<? super Integer> getComparator() {
            if (hasCharacteristics(Spliterator.SORTED))
                return null;
            throw new IllegalStateException();
        }
    }
}
