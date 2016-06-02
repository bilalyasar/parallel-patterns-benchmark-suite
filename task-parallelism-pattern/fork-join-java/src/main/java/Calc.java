import java.util.concurrent.RecursiveAction;

public class Calc extends RecursiveAction {

    float[][] matrixA, matrixB, matrixC;
    int i, j;

    Calc(float[][] matrixA, float[][] matrixB, float[][] matrixC, int i) {
        this.matrixA = matrixA;
        this.matrixB = matrixB;
        this.matrixC = matrixC;
        this.i = i;
    }

    @Override
    public void compute() {
        for (int j = 0; j < ForkJoinMatrixMultiplication.SIZE; j++)
            for (int k = 0; k < ForkJoinMatrixMultiplication.SIZE; k++)
                matrixC[i][j] += matrixA[i][k] * matrixB[k][j];
    }

}
