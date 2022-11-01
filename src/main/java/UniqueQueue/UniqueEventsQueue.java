package UniqueQueue;

import java.util.HashSet;
import java.util.Objects;

@SuppressWarnings("unchecked")
public class UniqueEventsQueue<T> implements UniqueQueue<T> {
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
    private Object[] queue;
    private final HashSet<T> content;
    private int size;
    private int getIndex;
    private int addIndex;

    public UniqueEventsQueue() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    public UniqueEventsQueue(int initialCapacity) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("Initial capacity can't be less than 1");
        }
        queue = new Object[initialCapacity];
        content = new HashSet<>(initialCapacity);
    }

    private int shiftIndex(int ind) {
        return ind + 1 >= queue.length ? 0 : ind + 1;
    }

    @Override
    public boolean add(T el) {
        Objects.requireNonNull(el);

        if (!content.add(el)) {
            return false;
        }
        if (size >= queue.length) {
            increaseCapacity();
        }
        queue[addIndex] = el;
        addIndex = shiftIndex(addIndex);
        size++;

        return true;
    }

    private void increaseCapacity() {
        int oldCapacity = queue.length;
        int newCapacity = oldCapacity +
                ((oldCapacity < 64) ? (oldCapacity + 2) : (oldCapacity >> 1));
        if (newCapacity - MAX_ARRAY_SIZE > 0) {
            throw new OutOfMemoryError();
        }
        queue = copyArrayWithShift(newCapacity);
        getIndex = 0;
        addIndex = size;
    }

    @Override
    public T get() {
        T el = (T) queue[getIndex];
        if (el != null) {
            final Object[] items = queue;
            items[getIndex] = null;
            getIndex = shiftIndex(getIndex);
            size--;
            content.remove(el);
        }
        return el;
    }

    public T peek() {
        return (T) queue[getIndex];
    }

    public int size() {
        return size;
    }

    private Object[] copyArrayWithShift(int capacity) {
        Object[] newArr = new Object[capacity];
        int j = getIndex - 1;
        for (int i = 0; i < size; i++) {
            newArr[i] = queue[j = shiftIndex(j)];
        }
        return newArr;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof UniqueEventsQueue)) {
            return false;
        }
        UniqueEventsQueue<?> eqQueue = (UniqueEventsQueue<?>) o;
        boolean equal = eqQueue.size() == size;
        if (equal) {
            int j = eqQueue.getIndex - 1;
            int k = 0;
            for (int i = getIndex; k++ < size; i = shiftIndex(i)) {
                if (!Objects.equals(queue[i], eqQueue.queue[j = shiftIndex(j)])) {
                    equal = false;
                    break;
                }
            }
        }
        return equal;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        int j = 0;
        for (int i = getIndex; j++ < size; i = shiftIndex(i)) {
            sb.append(queue[i]);
            sb.append(j == size ? ']' : ", ");
        }
        return sb.toString();
    }

}