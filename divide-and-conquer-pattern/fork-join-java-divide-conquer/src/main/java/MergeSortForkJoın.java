import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

public class MergeSortForkJoın {
    static int totalNumbers = 8388608;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        long ms = System.currentTimeMillis();
        int[] numbers = new int[totalNumbers];
        Random random = new Random();
        for (int i = 0; i < totalNumbers; i++) {
            numbers[i] = random.nextInt() % 5000;
        }

        MergeSortTask task = new MergeSortTask(numbers);
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        forkJoinPool.invoke(task);
        System.out.println(System.currentTimeMillis() - ms);

        System.exit(0);

    }
}