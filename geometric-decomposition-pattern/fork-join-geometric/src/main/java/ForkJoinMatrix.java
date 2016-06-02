import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RecursiveAction;

import static java.util.concurrent.ForkJoinTask.invokeAll;


public class ForkJoinMatrix {
    static int NX = 50000000;
    static double LEFTVAL = 1.0;
    static double RIGHTVAL = 10.0;
    static double NSTEPS = 10;
    static double[] uk = new double[NX + 2];
    static double[] ukp1 = new double[NX + 2];
    static double[] temp;
    static int num_thrd = 32;
    static double dx = 1.0 / NX;
    static double dt = 0.5 * dx * dx;

    static void initialize(double uk[], double ukp1[]) {
        uk[0] = LEFTVAL;
        uk[NX - 1] = RIGHTVAL;
        for (int i = 1; i < NX - 1; ++i)
            uk[i] = 0.0;
        for (int i = 0; i < NX; ++i)
            ukp1[i] = uk[i];
    }


    public static void main(String[] args) throws ExecutionException, InterruptedException {

        long ms = System.currentTimeMillis();
        initialize(uk, ukp1);

        for (int k = 0; k < NSTEPS; ++k) {
//            #pragma omp parallel for schedule( static)
   /* compute new values */
            ArrayList<Task> list = new ArrayList<Task>();
            for (int i = 1; i <= num_thrd; i++) {
                int start = Math.max((NX / (num_thrd)) * (i - 1), 1);
                int end = Math.min(start + NX / num_thrd, NX);
                list.add(new Task(start, end));

            }
            Collection<Task> l = invokeAll(list);
            Iterator it = l.iterator();
            while(it.hasNext()){
                Task t = (Task) it.next();
                t.get();
            }

//            for (int i = 1; i < NX - 1; ++i) {
//                ukp1[i] = uk[i] + (dt / (dx * dx)) * (uk[i + 1] - 2 * uk[i] + uk[i - 1]);
//            }

   /* "copy" ukp1 to uk by swapping pointers */
            temp = ukp1;
            ukp1 = uk;
            uk = temp;

//            printValues(uk, k);
        }
//        return 0;

        System.out.println(System.currentTimeMillis() - ms);
    }

    static class Task extends RecursiveAction {
        int start, end;

        Task(int start, int end) {
//            System.out.println(start + " " + end);
            this.start = start;
            this.end = end;
        }

        @Override
        public void compute() {
            for (int i = start; i < end; i++) {
                ukp1[i] = uk[i] + (dt / (dx * dx)) * (uk[i + 1] - 2 * uk[i] + uk[i - 1]);
            }
        }
    }
}

  