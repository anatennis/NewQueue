import java.util.*;

/**
 * Type parameters:
 * <T> – the type of elements held in this queue
 * We need to implement a queue with following property.
 * If we are to add an object which is already in the queue (using equals semantic) nothing should happen.
 * If there is still no such object in the queue (again, using equals semantic)
 * then the object is added to the end of the queue.
 * Rationale is that we do not want to have several same objects in the queue.
 * This could be especially useful when objects are events to recalculate something
 * and there is no sense to have several such events in the queue.
 * <p>
 * Let's call our queue UniqueEventsQueue. Let it has just two methods like add() and get().
 */
@SuppressWarnings("unchecked")
public class UniqueEventsQueue<T> {
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
    private Object[] queue;
    int size;

    public UniqueEventsQueue() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    public UniqueEventsQueue(int initialCapacity) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("Initial capacity can't be less than 1");
        }
        queue = new Object[initialCapacity];
    }

    public boolean add(T el) {
        if (el == null) {
            throw new NullPointerException("Elements can't be equal to null");
        }
        for (int i = 0; i < size; i++) {
            if (el.equals(queue[i])) {
                return false;
            }
        }
        if (size >= queue.length) {
            increaseCapacity();
        }
        queue[size++] = el;
        return true;
    }

    private void increaseCapacity() {
        int oldCapacity = queue.length;
        // Double size if small; else grow by 50%
        int newCapacity = oldCapacity + ((oldCapacity < 64) ?
                (oldCapacity + 2) :
                (oldCapacity >> 1));
        if (newCapacity - MAX_ARRAY_SIZE > 0) {
            throw new OutOfMemoryError();
        }
        queue = Arrays.copyOf(queue, newCapacity);
    }

    public T get() {
        if (peek() == null) {
            return null;
        }
        T el = (T) queue[0];
        final Object[] items = queue;
        if (size > 1) {
            for (int i = 1; i < size; i++) {
                items[i-1] = items[i];
            }
        }
        items[--size] = null;

        return el;
    }

//    public T get2() {
//        if (peek() == null) {
//            return null;
//        }
//        T el = (T) queue[0];
//        if (size > 1) {
//            final Object[] items = queue;
//            System.arraycopy(queue, 1, items, 0, queue.length - 1);
//        } else if (size == 1) {
//            queue[0] = null;
//        }
//        size--;
//
//        return el;
//    }

    public T peek() {
        return (T) queue[0];
    }

    public int size() {
        return size;
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
            for (int i = 0; i < size; i++) {
                if (!Objects.equals(queue[i], eqQueue.queue[i])) {
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
        for (int i = 0; i < queue.length; i++) {
            sb.append(queue[i]);
            sb.append(i == queue.length - 1 ? ']' : ", ");
        }
        return sb.toString();
    }
}
