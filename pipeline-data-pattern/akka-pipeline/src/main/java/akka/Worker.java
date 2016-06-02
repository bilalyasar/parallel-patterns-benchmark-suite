package akka;

import akka.actor.Props;
import akka.actor.UntypedActor;
import scala.collection.mutable.ArraySeq;

import static akka.Master.SIZE;
import static akka.Master.queue1;
import static akka.Master.queue2;

public class Worker extends UntypedActor {

    @Override
    public void onReceive(Object message) {
        for (int i = 0; i < SIZE; i++) {
            if (message instanceof Worker1) queue1.add(i);
            if (message instanceof Worker2) {
                while (queue1.isEmpty()) ;
                queue1.poll();
                queue2.add(i);
            }

        }
        getSender().tell(new Result(), getSelf());
    }

    public static Props createWorker() {
        return Props.create(Worker.class, new ArraySeq<Object>(0));
    }
}
