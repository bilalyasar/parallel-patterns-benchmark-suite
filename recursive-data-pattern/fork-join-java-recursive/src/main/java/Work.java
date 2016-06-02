import java.util.concurrent.RecursiveAction;

public class Work extends RecursiveAction {
    int start, end, j;

    public Work(int start, int end, int j) {
        this.start = start;
        this.end = end;
        this.j = j;
    }

    @Override
    protected void compute() {
        int tt = 1 << this.j;
        for (int i = this.start; i < this.end; i++) {
            if (i - tt >= 0)
                PartialSumTree.t[i] = PartialSumTree.numArray[PartialSumTree.toread][i] + PartialSumTree.numArray[PartialSumTree.toread][i - tt];
            else PartialSumTree.t[i] = PartialSumTree.numArray[PartialSumTree.toread][i];
        }
    }
}
