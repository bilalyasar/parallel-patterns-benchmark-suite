package com.examples.akka.akka;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.RoundRobinRouter;
import scala.collection.mutable.ArraySeq;

import static com.examples.akka.Akka.millis;

public class Master extends UntypedActor {
    static int SIZE = 150000000;
    static int n = SIZE, size = 150000000;
    int SLICE = 20;
    static int toread = 1;
    static int numArray[][] = new int[2][SIZE + 1];
    static int t[];
    static int finish;

    public int count = 0;
    public static final long[][] MATRIX = new long[1000][1000];
    private ActorRef workerRouter;

    public Master() {
        MATRIX[3][1] = 10;
        workerRouter = this.getContext().actorOf(Worker.createWorker().withRouter(new RoundRobinRouter(32)), "workerRouter");
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof Calculate) {
            for (int i = 0; i < n; i++)
                numArray[0][i] = i % 10;

            for (int j = 0; Math.pow(2, j) < size; j++) {
                toread = 1 - toread;

                if (toread == 1) t = numArray[0];
                else t = numArray[1];
                for (int i = 1; i < SLICE; i++) {
                    int start = Math.max(1, (size / SLICE) * (i - 1));
                    int end = Math.min(size + 1, start + size / SLICE);
                    finish++;
                    workerRouter.tell(new Work(start, end, j), getSelf());
                }

            }

        } else if (message instanceof Result) {
            count++;
            if (count == finish) {
                getContext().system().shutdown();
            }
        } else {
            unhandled(message);
        }
    }

    public static Props createMaster() {
        return Props.create(Master.class, new ArraySeq<Object>(0));
    }

    @Override
    public void postStop() {
        System.out.println(System.currentTimeMillis() - millis);

    }
}
