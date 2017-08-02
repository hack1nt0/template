package template.collection;

import template.collection.sequence.ArrayUtils;

import java.util.*;
import java.util.function.IntConsumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

/**
 * Created by dy on 2017/2/8.
 *
 *  Size-Resizing Array List & Stack & Queue for ints or indexes.
 */
public class IntArrayList implements IntList {
    private int[] arr;
    private int from, to;
    private int N;
    private boolean sizeFixed;

    public IntArrayList(int initCapacity, boolean sizeFixed) {
        arr = new int[initCapacity];
        this.sizeFixed = sizeFixed;
    }

    public IntArrayList(int initCapacity) {
        this(initCapacity, false);
    }

    public IntArrayList() {
        this(1, false);
    }

    public int getFirst() {
        if (isEmpty()) throw new NoSuchElementException("The queue underflow.");
        return arr[from];
    }

    public int getLast() {
        if (isEmpty()) throw new NoSuchElementException("The queue underflow.");
        return arr[to - 1];
    }

    public int removeFirst() {
        if (isEmpty()) throw new NoSuchElementException("The queue underflow.");
        int first = arr[from++];
        if (from == arr.length) from = 0;
        N--;
        if (!sizeFixed) if (N > 0 && N == arr.length / 4) resize(arr.length / 2);
        return first;
    }

    public int removeLast() {
        if (isEmpty()) throw new NoSuchElementException("The queue underflow.");
        to--;
        if (to < 0) to = arr.length - 1;
        int last = arr[to];
        N--;
        if (!sizeFixed) if (N > 0 && N == arr.length / 4) resize(arr.length / 2);
        return last;
    }

    public boolean isEmpty () {
        return N == 0;
    }

    public void addFirst(int o) {
        if (size() == arr.length) {
            if (sizeFixed) throw new RuntimeException("Queue overflow.");
            resize(arr.length * 2);
        }
        from--;
        if (from < 0) from = arr.length - 1;
        arr[from] = o;
        N++;
    }

    public boolean addLast(int o) {
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

    public boolean contains(int o) {
        for (int i : arr) if (i == o) return true;
        return false;
    }

    public int size() {
        return N;
    }

    private void resize(int capacity) {
        //System.err.println(capacity + " " + N + " " + arr.length);
        int[] temp = new int[capacity];
        for (int i = 0; i < N; ++i) temp[i] = arr[(from + i) % arr.length];
        arr = temp;
        from = 0;
        to = N;
    }

    public void clear() {
        from = to = 0;
        N = 0;
    }

    @Override
    public boolean equals(IntCollection o) {
        return false;
    }

    @Override
    public Spliterator.OfInt spliterator() {
        return new IntCyclicArraySpliterator(arr, from, size());
    }

    @Override
    public IntStream stream() {
        return StreamSupport.intStream(spliterator(), false);
    }

    @Override
    public IntStream parallelStream() { throw new UnsupportedOperationException();}

    public int peek() {
        return getFirst();
    }

    public int poll() {
        return removeFirst();
    }

    public int peekLast() {
        return getLast();
    }

    public int pollLast() {
        return removeLast();
    }

    public boolean add(int o) {
        return addLast(o);
    }

    @Override
    public boolean removeOne(int o) { throw new RuntimeException();}

    @Override
    public boolean containsAll(IntCollection c) { throw new UnsupportedOperationException();}

    @Override
    public boolean addAll(IntCollection c) { throw new UnsupportedOperationException();}

    @Override
    public boolean removeAll(IntCollection c) { throw new UnsupportedOperationException();}

    @Override
    public boolean removeIf(Predicate<? super Integer> filter) { throw new UnsupportedOperationException();}

    @Override
    public boolean retainAll(Collection<?> c) {throw new UnsupportedOperationException();}

    public void push(int o) {
        addLast(o);
    }

    public int pop() {
        return pollLast();
    }

    @Override
    public PrimitiveIterator.OfInt iterator() {
        return new PrimitiveIterator.OfInt() {
            int first = from;
            int size = N;
            @Override
            public boolean hasNext() {
                return size-- > 0;
            }

            @Override
            public int nextInt() {
                int n = arr[first];
                first++;
                if (first == arr.length) first = 0;
                return n;
            }
        };
    }

    @Override
    public int[] toArray() {
        return arr;
    }

    public Integer[] toArray(Integer[] a) {
        return (Integer[]) ArrayUtils.inbox(arr);
    }

    public PrimitiveIterator.OfInt stackIterator() {
        return new PrimitiveIterator.OfInt() {
            int last = to;
            int size = N;
            @Override
            public boolean hasNext() {
                return size-- > 0;
            }

            /**
             * Returns the next {@code int} element in the iteration.
             *
             * @return the next {@code int} element in the iteration
             * @throws NoSuchElementException if the iteration has no more elements
             */
            @Override
            public int nextInt() {
                last--;
                if (last < 0) last = arr.length - 1;
                return arr[last];
            }
        };
    }

    @Override
    public int get(int index) {
        if (index >= size()) throw new ArrayIndexOutOfBoundsException();
        int i = from + index;
        if (i >= arr.length) i -= arr.length;
        return arr[i];
    }

    @Override
    public int set(int index, int element) {
        if (index >= size()) throw new ArrayIndexOutOfBoundsException();
        int i = from + index;
        if (i >= arr.length) i -= arr.length;
        int old = arr[i];
        arr[i] = element;
        return old;
    }

    @Override
    public void add(int index, int element) { throw new UnsupportedOperationException();}


    @Override
    public int remove(int index) { throw new UnsupportedOperationException();}


    //Copied from java.util.Spliterators
    /**
     * A Spliterator.OfInt designed for use by sources that traverse and split0
     * elements maintained in an unmodifiable {@code int[]} array.
     */
    static final class IntCyclicArraySpliterator implements Spliterator.OfInt {
        private final int[] array;
        private int from;        // current from, modified on advance/split0
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
            Spliterator.OfInt ans = new IntCyclicArraySpliterator(array, from, size >> 1);
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
