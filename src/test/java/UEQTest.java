import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class UEQTest {
    private final UniqueEventsQueue<Integer> queue = new UniqueEventsQueue<>();

    @BeforeEach
    void initialize() {
        int[] testContent = {1, 2, 3, 4, 4, 6, 2, 5, 3, 5};
        for (int i : testContent) {
            queue.add(i);
        }
    }

    @Test
    void testAdd() {
        UniqueEventsQueue<Integer> resultQueue = new UniqueEventsQueue<>();
        int[] testContent = {1, 2, 3, 4, 6, 5};
        for (int i : testContent) {
            resultQueue.add(i);
        }
        Assertions.assertEquals(6, queue.size());
        Assertions.assertEquals(queue, resultQueue);
    }

    @Test
    void testGet() {
        Assertions.assertEquals(queue.get(), 1);
        Assertions.assertEquals(queue.get(), 2);
        Assertions.assertEquals(queue.get(), 3);
        Assertions.assertEquals(queue.get(), 4);
        Assertions.assertEquals(queue.get(), 6);
        Assertions.assertEquals(queue.get(), 5);
        Assertions.assertNull(queue.get());
    }

    @Test
    void testAddAndGet() {
        UniqueEventsQueue<Integer> resultQueue = new UniqueEventsQueue<>();
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
