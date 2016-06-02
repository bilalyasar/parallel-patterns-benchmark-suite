package com.examples.akka.akka;

public class Result {

    public long result;
    public int row;
    public int column;

    public Result(int row, int column, long result) {
        this.row = row;
        this.column = column;
        this.result = result;
    }
}
