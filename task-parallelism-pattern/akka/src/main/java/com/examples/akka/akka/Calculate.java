package com.examples.akka.akka;

public class Calculate {
    int column, row;

    public Calculate(int column) {
        this.column = column;
    }


    public int calc() {
        int result = 0;
        for (int j = 0; j < Master.SIZE; j++) {
            result = 0;
            row = j;
            for (int i = 0; i < Master.SIZE; i++) {
                result += Master.MATRIX[row][i] * Master.MATRIX[i][column];
            }
            Master.RESULT_MATRIX[row][column] = result;
        }
        return 0;
    }
}
