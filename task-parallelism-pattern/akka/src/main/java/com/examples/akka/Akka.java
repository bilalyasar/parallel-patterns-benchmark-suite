package com.examples.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.examples.akka.akka.Calculate;
import com.examples.akka.akka.Master;

public class Akka {

    public static  Long millis;
    public static void main(String[] args) throws InterruptedException {
        millis = System.currentTimeMillis();
        new Akka().run();
    }

    private void run() throws InterruptedException {
        ActorSystem system = ActorSystem.create("CalcSystem");
        ActorRef master = system.actorOf(Master.createMaster(), "master");
        master.tell(new Calculate(0), ActorRef.noSender());
    }
}
