package com.examples.akka.akka;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.RoundRobinRouter;
import scala.collection.mutable.ArraySeq;

import static com.examples.akka.Akka.millis;

public class Master extends UntypedActor {

    public int count = 0;
    public static int SIZE = 2000;
    public static final long[][] MATRIX = new long[SIZE][SIZE];
    public static final long[][] RESULT_MATRIX = new long[SIZE][SIZE];
    private ActorRef workerRouter;

    public Master() {
        MATRIX[3][1] = 10;
        workerRouter = this.getContext().actorOf(Worker.createWorker().withRouter(new RoundRobinRouter(32)), "workerRouter");
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof Calculate) {
            processMessages();
        } else if (message instanceof Result) {
            count++;
            if (count == SIZE)
                getContext().system().shutdown();
        } else {
            unhandled(message);
        }
    }

    private void processMessages() {
        for (int i = 0; i < SIZE; i++) {
            workerRouter.tell(new Work(i), getSelf());
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
