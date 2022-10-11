import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@SuppressWarnings("unchecked")
public class UniqueEventsConcurrentQueue<T> implements UniqueQueue<T> {
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
    private Object[] queue;
    private final HashSet<T> content;
    private final ReadWriteLock lock;
    private int size;
    private int getIndex;
    private int addIndex;

    public UniqueEventsConcurrentQueue() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    public UniqueEventsConcurrentQueue(int initialCapacity) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("Initial capacity can't be less than 1");
        }
        queue = new Object[initialCapacity];
        content = new HashSet<>(initialCapacity);
        lock = new ReentrantReadWriteLock();
    }

    @Override
    public boolean add(T el) {
        Objects.requireNonNull(el);
        final Lock lock = this.lock.writeLock();
        lock.lock();
        try {
            if (!content.add(el)) {
                return false;
            }
            if (size >= queue.length) {
                increaseCapacity();
            }
            queue[addIndex] = el;
            addIndex = shiftIndex(addIndex);
            size++;
        } finally {
            lock.unlock();
        }
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
        final Lock lock = this.lock.writeLock();
        lock.lock();
        try {
            T el = (T) queue[getIndex];
            if (el != null) {
                final Object[] items = queue;
                items[getIndex] = null;
                getIndex = shiftIndex(getIndex);
                size--;
                content.remove(el);
            }
            return el;
        } finally {
            lock.unlock();
        }
    }

    public T peek() {
        final Lock lock = this.lock.readLock();
        lock.lock();
        try {
            return (T) queue[getIndex];
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        final Lock lock = this.lock.readLock();
        lock.lock();
        try {
            return size;
        } finally {
            lock.unlock();
        }
    }
    public Object[] toArray() {
        final Lock lock = this.lock.readLock();
        lock.lock();
        try {
            return copyArrayWithShift(size);
        } finally {
            lock.unlock();
        }
    }

    private Object[] copyArrayWithShift(int capacity) {
        Object[] newArr = new Object[capacity];
        int j = getIndex - 1;
        for (int i = 0; i < size; i++) {
            newArr[i] = queue[j = shiftIndex(j)];
        }
        return newArr;
    }

    private int shiftIndex(int ind) {
        return ind + 1 >= queue.length ? 0 : ind + 1;
    }

    /**
     * Compares the specified queues for equality and execute Runnable task with guarantee that
     * during execution both queues are equal
     * Experimental
     */
    @SuppressWarnings("rawtypes")
    public static void doIfEqual(UniqueEventsConcurrentQueue queue1, UniqueEventsConcurrentQueue queue2, Runnable task) {
        final Lock fLock = queue1.lock.readLock();
        final Lock secLock = queue2.lock.readLock();
        if (System.identityHashCode(queue1) > System.identityHashCode(queue2)) {
            fLock.lock();
            secLock.lock();
            try {
                if (queue1.equals(queue2)) {
                    task.run();
                }
            } finally {
                secLock.unlock();
                fLock.unlock();
            }
        } else {
            secLock.lock();
            fLock.lock();
            try {
                if (queue1.equals(queue2)) {
                    task.run();
                }
            } finally {
                fLock.unlock();
                secLock.unlock();
            }
        }
    }

    /**
     * Compares the specified object with this queue for equality.
     * This operation may return misleading results if either queue is concurrently modified
     * during execution of this method.
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof UniqueEventsConcurrentQueue)) {
            return false;
        }
        Object[] eqQueue = ((UniqueEventsConcurrentQueue<?>) o).toArray();
        Object[] curQueue = toArray();

        boolean equal = eqQueue.length == curQueue.length;
        if (equal) {
            for (int i = 0; i < curQueue.length; i++) {
                if (!Objects.equals(curQueue[i], eqQueue[i])) {
                    equal = false;
                    break;
                }
            }
        }
        return equal;
    }

    @Override
    public String toString() {
        Object[] curQueue = toArray();
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < curQueue.length; i++) {
            sb.append(curQueue[i]);
            sb.append(i == curQueue.length - 1 ? ']' : ", ");
        }
        return sb.length() == 1 ? sb.append(']').toString() : sb.toString();
    }
}