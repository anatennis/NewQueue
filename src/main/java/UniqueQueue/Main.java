package UniqueQueue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static final Integer POISON_PILL = Integer.MAX_VALUE;
    static UniqueEventsConcurrentQueue<Integer> queue = new UniqueEventsConcurrentQueue<Integer>();
    //static UniqueQueue.UniqueEventsConcurrentQueue<Integer> queue2 = new UniqueQueue.UniqueEventsConcurrentQueue<>();
    //static UniqueQueue.UniqueEventsQueue<Integer> queue = new UniqueQueue.UniqueEventsQueue<>();

    public static void main(String[] args) {
        UniqueQueue<Integer> queue = new UniqueEventsConcurrentQueue<Integer>();

        ExecutorService executorService = null;
        try {
            executorService = Executors.newFixedThreadPool(4);
            executorService.execute(new Producer(queue, POISON_PILL));
            for (int i = 0; i < 2; i++) {
                executorService.execute(new Consumer(queue));
            }
            Thread.sleep(3000);
            executorService.execute(() -> {
                System.out.println("WAKE UP!");
                for (int i = 0; i < 10; i++) queue.add(2341 + i);
                queue.add(POISON_PILL);
            });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
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
                } catch (InterruptedException e) {
                    System.out.println("EXCEPTION " + e);
                }
                System.out.println("FOR " + Thread.currentThread().getName() + " Iteration: " + i);
            }
        }
    }

    public static class Producer implements Runnable {
        private final UniqueQueue<Integer> queue;
        private final int poison;

        @Override
        public void run() {
            try {
                process();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                queue.add(poison);
            }
        }

        private void process() throws InterruptedException {
            for (int i = 0; i < 20; i++) {
                System.out.println(Thread.currentThread().getName() + " [Producer] Put : " + i);
                queue.add(i);
                Thread.sleep(100);
            }
        }

        public Producer(UniqueQueue<Integer> queue, int poison) {
            this.queue = queue;
            this.poison = poison;
        }
    }

    public static class Consumer implements Runnable {
        private final UniqueQueue<Integer> queue;

        @Override
        public void run() {

            try {
                while (true) {
                    Integer take = queue.get();
                    if (POISON_PILL.equals(take)) {
                        System.out.println(Thread.currentThread().getName() + " I'M DEAD");
                        break;
                    }
                    process(take);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        private void process(Integer take) throws InterruptedException {
            System.out.println(Thread.currentThread().getName() + " [Consumer] Take : " + take);
            Thread.sleep(50);
        }

        public Consumer(UniqueQueue<Integer> queue) {
            this.queue = queue;
        }
    }
}