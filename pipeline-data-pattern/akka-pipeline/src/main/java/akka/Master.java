package akka;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.RoundRobinRouter;
import scala.collection.mutable.ArraySeq;

import java.util.concurrent.ConcurrentLinkedQueue;


public class Master extends UntypedActor {
    static int SIZE = 500000;
    static int finish=2;

    public int count = 0;
    public static ConcurrentLinkedQueue<Integer> queue2 = new ConcurrentLinkedQueue<>();
    public static ConcurrentLinkedQueue<Integer> queue1 = new ConcurrentLinkedQueue<>();
    private ActorRef workerRouter;

    public Master() {
        workerRouter = this.getContext().actorOf(Worker.createWorker().withRouter(new RoundRobinRouter(32)), "workerRouter");
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof Calculate) {
            workerRouter.tell(new Worker1(), getSelf());
            workerRouter.tell(new Worker2(), getSelf());
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

        System.out.println(System.currentTimeMillis() - Akka.millis);

    }
}
