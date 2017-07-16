package template.collection.sequence;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created by dy on 2017/2/8.
 *
 *  Queue for Integer or Indexes.
 */
public class ArrayQueue<T> implements Iterable<T>{
    private T[] arr;
    private int from, to;
    private int N;
    boolean sizeFixed;

    public ArrayQueue(int initCapacity, boolean sizeFixed) {
        arr = (T[])new Object[initCapacity];
        this.sizeFixed = sizeFixed;
    }

    public ArrayQueue(int initCapacity) {
        this(initCapacity, false);
    }

    public ArrayQueue() {
        this(1, false);
    }

    public T getFirst() {
        if (isEmpty()) throw new NoSuchElementException("The queue underflow.");
        return arr[from];
    }

    public T getLast() {
        if (isEmpty()) throw new NoSuchElementException("The queue underflow.");
        return arr[to - 1];
    }

    public T removeFirst() {
        if (isEmpty()) throw new NoSuchElementException("The queue underflow.");
        T first = arr[from++];
        if (from == arr.length) from = 0;
        N--;
        if (!sizeFixed) if (N > 0 && N == arr.length / 4) resize(arr.length / 2);
        return first;
    }

    public T removeLast() {
        if (isEmpty()) throw new NoSuchElementException("The queue underflow.");
        to--;
        if (to < 0) to = arr.length - 1;
        T last = arr[to];
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

    public void addLast(T o) {
        if (size() == arr.length) {
            if (sizeFixed) throw new RuntimeException("Queue overflow.");
            resize(arr.length * 2);
        }
        arr[to] = o;
        to++;
        if (to == arr.length) to = 0;
        N++;
    }

    public int size() {
        return N;
    }

    private void resize(int capacity) {
        //System.err.println(capacity + " " + N + " " + arr.length);
        T[] temp = (T[]) new Object[capacity];
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

    public void add(T o) {
        addLast(o);
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
            int first = from;
            int size = N;
            @Override
            public boolean hasNext() {
                return size-- > 0;
            }

            @Override
            public T next() {
                T n = arr[first];
                first++;
                if (first == arr.length) first = 0;
                return n;
            }
        };
    }

    public Iterator<T> stackIterator() {
        return new Iterator<T>() {
            int last = to;
            int size = N;
            @Override
            public boolean hasNext() {
                return size-- > 0;
            }

            @Override
            public T next() {
                last--;
                if (last < 0) last = arr.length - 1;
                return arr[last];
            }
        };
    }

}
