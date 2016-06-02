import java.util.ArrayList;

import static java.util.concurrent.ForkJoinTask.invokeAll;

public class PartialSumTree {

    static int SIZE = 15000;
    static int n = SIZE, size = 15000;
    static int SLICE = 32;
    static int toread = 1;
    static int numArray[][] = new int[2][SIZE + 1];
    public static int t[];
    static int finish;

    public static void main(String[] args) {


        long tm = System.currentTimeMillis();

        for (int i = 0; i < n; i++)
            numArray[0][i] = i % 10;

        for (int j = 0; Math.pow(2,j) < size; j++) {
            toread = 1 - toread;

            if (toread == 1) t = numArray[0];
            else t = numArray[1];
            ArrayList<Work> list = new ArrayList();
            for (int i = 1; i < SLICE; i++) {
                int start = Math.max(1, (size / SLICE) * (i - 1));
                int end = Math.min(size+1, start + size / SLICE);
                finish++;
                list.add(new Work(start, end, j));
            }
        invokeAll(list);
        }


        System.out.println(System.currentTimeMillis() - tm);
    }
}