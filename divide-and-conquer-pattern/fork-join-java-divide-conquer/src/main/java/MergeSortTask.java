import java.util.Arrays;
import java.util.List;
import java.util.concurrent.RecursiveTask;

class MergeSortTask extends RecursiveTask<int[]> {

    int[] arrayToMerged;

    public MergeSortTask(int[] arrayToMerged) {
        this.arrayToMerged = arrayToMerged;
    }

    @Override
    protected int[] compute() {
        if (arrayToMerged.length > 1) {

            List<int[]> partitionedArray = splitArrays();

            MergeSortTask task1 = new MergeSortTask(partitionedArray.get(0));
            MergeSortTask task2 = new MergeSortTask(partitionedArray.get(1));
            invokeAll(task1, task2);
            int[] array1 = task1.join();
            int[] array2 = task2.join();
            int[] mergedArray =
                    new int[array1.length + array2.length];

            mergeArrays(task1.join(), task2.join(), mergedArray);

            return mergedArray;
        }
        return arrayToMerged;
    }

    private List<int[]> splitArrays(){

        int [] partition1 = Arrays.copyOfRange(arrayToMerged, 0,
                arrayToMerged.length / 2);

        int [] partition2 = Arrays.copyOfRange(arrayToMerged,
                arrayToMerged.length / 2,
                arrayToMerged.length);
        return Arrays.asList(partition1,partition2);

    }

    private void mergeArrays(
            int[] firstArray,
            int[] secondArray,
            int[] resultArray) {

        int i = 0, j = 0, k = 0;

        while ((i < firstArray.length) && (j < secondArray.length)) {

            if (firstArray[i] < secondArray[j]) {
                resultArray[k] = firstArray[i++];
            } else {
                resultArray[k] = secondArray[j++];
            }

            k++;
        }

        if (i == firstArray.length) {

            for (int a = j; a < secondArray.length; a++) {
                resultArray[k++] = secondArray[a];
            }

        } else {

            for (int a = i; a < firstArray.length; a++) {
                resultArray[k++] = firstArray[a];
            }

        }
    }
}