import UniqueQueue.UniqueEventsConcurrentQueue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class UEQCTest {
    private final UniqueEventsConcurrentQueue<Integer> queue = new UniqueEventsConcurrentQueue<Integer>();

    @BeforeEach
    void initialize() {
        int[] testContent = {1, 2, 3, 4, 4, 6, 2, 5, 3, 5};
        for (int i : testContent) {
            queue.add(i);
        }
    }

    @Test
    void testAdd() throws InterruptedException {
        UniqueEventsConcurrentQueue<Integer> resultQueue = new UniqueEventsConcurrentQueue<Integer>();
        resultQueue.add(156);
        int[] testContent = {10, 11, 12, 13, 14, 15, 16, 17, 18, 19};
        for (int i : testContent) {
            queue.add(i);
            resultQueue.add(i);
        }
        System.out.println(queue);
        for (int i = 0; i < 6; i++) {
            queue.get();
        }
        System.out.println(queue);
        int[] testContent1 = {1, 2, 3, 4, 4, 6, 2, 5, 3, 5, 8};
        for (int i : testContent1) {
            queue.add(i);
            resultQueue.add(i);
        }

        resultQueue.get();

        System.out.println(queue);
        Assertions.assertEquals(17, queue.size());
        Assertions.assertEquals(queue, resultQueue);
    }

    @Test
    void testGet() throws InterruptedException {
        Assertions.assertEquals(queue.get(), 1);
        Assertions.assertEquals(queue.get(), 2);
        Assertions.assertEquals(queue.get(), 3);
        Assertions.assertEquals(queue.get(), 4);
        Assertions.assertEquals(queue.get(), 6);
        Assertions.assertEquals(queue.get(), 5);
    }

    @Test
    void testAddAndGet() throws InterruptedException {
        UniqueEventsConcurrentQueue<Integer> resultQueue = new UniqueEventsConcurrentQueue<Integer>();
        int[] testContent = {5, 7};
        for (int i : testContent) {
            resultQueue.add(i);
        }

        for (int i = 0; i < 5; i++) {
            int a = queue.get();
            queue.add(++a);
        }
        Assertions.assertEquals(2, queue.size());
        Assertions.assertEquals(queue, resultQueue);
    }
}
