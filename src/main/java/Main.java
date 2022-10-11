import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {


    static UniqueEventsConcurrentQueue<Integer> queue = new UniqueEventsConcurrentQueue<>();
    static UniqueEventsConcurrentQueue<Integer> queue2 = new UniqueEventsConcurrentQueue<>();
    //static UniqueEventsQueue<Integer> queue = new UniqueEventsQueue<>();

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            queue.add(i);
        }
        System.out.println("QUEUE " + queue);
        for (int i = 121; i < 132; i+=2) {
            queue2.add(i);
        }
        System.out.println("QUEUE2 " + queue2);

        ExecutorService executorService = null;
        try {
            executorService = Executors.newFixedThreadPool(5);
            for (int i = 0; i < 5; i++) {
                executorService.execute(new Concurrency());
            }
        } finally {
            if (executorService != null)
                executorService.shutdown();
        }
    }

    static class Concurrency extends Thread {
        @Override
        public void run() {
            System.out.println("I AM THREAD " + Thread.currentThread().getName());

            for (int i = 1; i <= 21; i++) {
                try {
                    Thread.sleep(1);
                    System.out.println("THREAD " + Thread.currentThread().getName() + " GETS " + queue.get());
                    System.out.println("THREAD " + Thread.currentThread().getName() + " with " + queue);
                    if (i % 2 == 0) {
                        queue.add(i + 111);
                    }
                    System.out.println("THREAD " + Thread.currentThread().getName() + " AFTER with " + queue);
                    System.out.println(" EQUALS ? " + queue.equals(queue2));
                } catch (InterruptedException e) {
                    System.out.println("EXCEPTION " + e);
                }
                System.out.println("Iteration: " + i);
                UniqueEventsConcurrentQueue.doIfEqual(queue, queue2, () -> {
                    System.out.println("THREAD " + Thread.currentThread().getName());
                    System.out.println("THREAD " + Thread.currentThread().getName() + " QUEUE1 = " + queue);
                    System.out.println("THREAD " + Thread.currentThread().getName() + " QUEUE2 = " + queue2);
                });
            }
        }
    }
}