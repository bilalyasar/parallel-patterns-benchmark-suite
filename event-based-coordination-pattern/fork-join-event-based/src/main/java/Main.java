import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;


public class Main {
    static long tm;
    static int SIZE = 300000;
    private static final int PARALLELISM = 100;

    private static final int QUEUE_CAPACITY = 10;

    private static final Random r = new Random();

    private final BlockingQueue<Task> queue;
    private final BlockingQueue<Task> queue2;


    private final ExecutorService pool;

    private List<Producer<Task>> producers;

    private List<Consumer<Task>> consumers;

    private List<Consumer2<Task>> consumers2;

    private static class Producer<T> implements Runnable {

        private final BlockingQueue queue;

        public Producer(BlockingQueue<T> queue) {
            assert queue != null;
            this.queue = queue;
        }

        public void run() {
            for (int i = 0; i < SIZE; i++) {
                int input = i;
//                Task task = new Task(input);
                try {
                    queue.put(i);
//                    Thread.sleep(input);
                } catch (Throwable t) {
                    throw new IllegalStateException(t);
                }
            }
        }
    }

    private static class Consumer<Task> implements Runnable {

        private final ExecutorService pool;

        private final BlockingQueue<Task> queue;
        private final BlockingQueue<Task> queue2;

        public Consumer(BlockingQueue<Task> queue, BlockingQueue<Task> queue2, ExecutorService pool) {
            assert queue != null;
            assert pool != null;
            this.queue = queue;
            this.queue2 = queue2;
            this.pool = new ForkJoinPool(PARALLELISM);
        }

        public void run() {
            Task task;
            for (int i = 0; i < SIZE; i++) {
                try {
                    task = queue.take();
                    queue2.put(task);
//                    Future<Integer> result = pool.submit((Callable<Integer>) task);
//                    System.out.println(Thread.currentThread().getName() + ", Task:" + task.toString() +
//                            ", Result:" + result.get() + ", Time:" + System.currentTimeMillis() + ", Queue size:" + queue.count);
                } catch (Throwable t) {
                    throw new IllegalStateException(t);
                }
            }
            System.out.println(System.currentTimeMillis() - tm);
        }

    }


    private static class Consumer2<Task> implements Runnable {

        private final ExecutorService pool;

        private final BlockingQueue<Task> queue;
        private final BlockingQueue<Task> queue2;

        public Consumer2(BlockingQueue<Task> queue, BlockingQueue<Task> queue2, ExecutorService pool) {
            assert queue != null;
            assert pool != null;
            this.queue = queue;
            this.queue2 = queue2;
            this.pool = new ForkJoinPool(PARALLELISM);
        }

        public void run() {
            Task task;
            for (int i = 0; i < SIZE; i++) {
                try {
                    task = queue2.take();
//                    Future<Integer> result = pool.submit((Callable<Integer>) task);
//                    System.out.println(Thread.currentThread().getName() + ", Task:" + task.toString() +
//                            ", Result:" + result.get() + ", Time:" + System.currentTimeMillis() + ", Queue size:" + queue.count);
                } catch (Throwable t) {
                    throw new IllegalStateException(t);
                }
            }
            System.out.println(System.currentTimeMillis() - tm);
        }

    }

    private static class Task implements Callable<Integer> {
        int result;
        final int input;

        public Task(int input) {
            this.input = input;
        }

        public Integer call() throws InterruptedException {
            int timeSpend = r.nextInt(1000);
//            Thread.sleep(timeSpend);
            result = input * input;
            return result;
        }
    }

    public Main() {
        this.queue = new BlockingQueue<Task>(QUEUE_CAPACITY);
        this.queue2 = new BlockingQueue<Task>(QUEUE_CAPACITY);
        this.pool = new ForkJoinPool(PARALLELISM);
        this.producers = new ArrayList<Producer<Task>>();
        producers.add(new Producer(queue));
        this.consumers = new ArrayList<Consumer<Task>>();
        this.consumers2 = new ArrayList<Consumer2<Task>>();
        consumers.add(new Consumer(queue, queue2, pool));
        consumers2.add(new Consumer2(queue, queue2, pool));
    }

    public void start() {
        for (Producer p : producers) {
            new Thread(p).start();
            break;
        }

        for (Consumer c : consumers) {
            new Thread(c).start();
            break;
        }
        for (Consumer2 c : consumers2) {
            new Thread(c).start();
            break;
        }
    }

    public static void main(String[] args) {
        tm = System.currentTimeMillis();
        Main example = new Main();
        example.start();

    }
}