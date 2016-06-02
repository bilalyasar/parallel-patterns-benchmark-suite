import java.util.ArrayList;
import java.util.List;

import static java.util.concurrent.ForkJoinTask.invokeAll;

public class ForkJoinMatrixMultiplication {
    public static int SIZE = 2000;

    public static void main(String[] args) {
        long millis = System.currentTimeMillis();
        float[][] a = generateRandomMatrix(SIZE, SIZE);
        float[][] b = generateRandomMatrix(SIZE, SIZE);
        float[][] c = new float[SIZE][SIZE];
        List<Calc> arrayList = new ArrayList<Calc>();
        for (int i = 0; i < SIZE; i++) {
            arrayList.add(new Calc(a, b, c, i));
        }
        invokeAll(arrayList);
        System.out.println(System.currentTimeMillis() - millis);
    }

    public static float[][] generateRandomMatrix(int m, int n) {
        float[][] C = new float[m][n];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                C[i][j] = (i) % 10;
        return C;
    }

}


