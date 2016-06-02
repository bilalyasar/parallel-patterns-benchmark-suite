/**
 * Created by bilal on 21/03/16.
 */
public class Sequential {
    static int SIZE = 1000;

    public static void main(String[] args) {
        long millis = System.currentTimeMillis();
        double[][] A = generateRandomMatrix(SIZE, SIZE);
        double[][] B = generateRandomMatrix(SIZE, SIZE);
        multiplyMatrixes(A, B);
        System.out.println("TIME: " + (System.currentTimeMillis() - millis));
    }


    public static double[][] multiplyMatrixes(double[][] matrixA, double[][] matrixB) {
        int rowA = matrixA.length;
        int columnA = matrixA[0].length;
        int rowB = matrixB.length;
        int columnB = matrixB[0].length;
        double[][] matrixC = new double[rowA][columnB];
        for (int i = 0; i < rowA; i++)
            for (int j = 0; j < columnB; j++)
                for (int k = 0; k < columnA; k++)
                    matrixC[i][j] += matrixA[i][k] * matrixB[k][j];
        return matrixC;
    }

    public static double[][] generateRandomMatrix(int m, int n) {
        double[][] C = new double[m][n];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                C[i][j] = 0;
//                C[i][j] = Math.random();
        return C;
    }

}

