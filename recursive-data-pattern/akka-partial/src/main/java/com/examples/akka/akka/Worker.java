package com.examples.akka.akka;

import akka.actor.Props;
import akka.actor.UntypedActor;
import scala.collection.mutable.ArraySeq;

import static com.examples.akka.akka.Master.numArray;
import static com.examples.akka.akka.Master.t;
import static com.examples.akka.akka.Master.toread;

public class Worker extends UntypedActor {

    @Override
    public void onReceive(Object message) {
        if (message instanceof Work) {
            Work work = (Work) message;
            int j = work.j;
            int tt = 1 << j;
            for (int i = work.start; i < work.end; i++) {
                if (i - tt >= 0)
                    t[i] = numArray[toread][i] + numArray[toread][i - tt];
                else t[i] = numArray[toread][i];
            }
            getSender().tell(new Result(), getSelf());
        } else
            unhandled(message);
    }

    public static Props createWorker() {
        return Props.create(Worker.class, new ArraySeq<Object>(0));
    }
}
