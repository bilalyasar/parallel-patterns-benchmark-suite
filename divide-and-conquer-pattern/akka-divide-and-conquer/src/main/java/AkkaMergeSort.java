import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import akka.routing.RoundRobinRouter;
import com.google.common.collect.ImmutableList;
import scala.concurrent.duration.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.singletonList;

public class AkkaMergeSort {
    private final int numberOfWorkers = 32;

    public static void main(String[] args) {
        new AkkaMergeSort().mergeSort(generateRandomNumbers(8388608));
    }

    private static List<Integer> generateRandomNumbers(int n) {
        List<Integer> numbers = new ArrayList<>(n);
        Random random = new Random();
        for (int i = 0; i < n; i++) {
            numbers.add(random.nextInt());
        }
        return numbers;
    }

    public List<Integer> mergeSort(Iterable<Integer> unsortedArray) {
        ActorSystem system = ActorSystem.create("MergesortSystem");
        final ActorRef listener = system.actorOf(new Props(Listener.class), "listener");

        // create the master
        ActorRef master = system.actorOf(new Props(new UntypedActorFactory() {
            @Override
            public Actor create() throws Exception {
                return new Master(numberOfWorkers, listener);
            }
        }), "master");
        master.tell(new MergeSort(unsortedArray), ActorRef.noSender());
        return Collections.emptyList();
    }

    public static class Listener extends UntypedActor {
        @Override
        public void onReceive(Object message) throws Exception {
            if (message instanceof Sorted) {
                getContext().system().shutdown();
            } else {
                unhandled(message);
            }
        }
    }

    private static class Master extends UntypedActor {
        private final long start = System.currentTimeMillis();
        private final ActorRef listener;
        private final ActorRef workerRouter;
        private final Stack<Merged> stack;

        private int originalSize;

        public Master(final int nrOfWorkers, ActorRef listener) {
            this.listener = listener;
            stack = new Stack<>();
            workerRouter = this.getContext().
                    actorOf(new Props(Worker.class).
                            withRouter(new RoundRobinRouter(nrOfWorkers)), "workerRouter");
        }

        @Override
        public void onReceive(Object message) throws Exception {
            if (message instanceof MergeSort) {
                List<Integer> work = ((MergeSort) message).getWork();
                originalSize = work.size();
                sendMergeRequestsForEveryTwoNumbers(work);
            } else if (message instanceof Merged) {
                returnOrNextMerge((Merged) message);
            } else {
                unhandled(message);
            }
        }

        private void sendMergeRequestsForEveryTwoNumbers(List<Integer> work) {
            if (work.size() % 2 == 0) {
                for (int i = 0; i < work.size(); i = i + 2) {
                    workerRouter.tell(new Merge(singletonList(work.get(i)), singletonList(work.get(i + 1))), getSelf());
                }
            } else {
                for (int i = 0; i < work.size(); i = i + 2) {
                    workerRouter.tell(new Merge(singletonList(work.get(i)), Collections.<Integer>emptyList()), getSelf());
                }
            }
        }

        private void returnOrNextMerge(Merged message) {
            if (message.getList().size() == originalSize) {
                listener.tell(
                        new Sorted(message.getList(), Duration.create(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS)),
                        getSelf());
                getContext().stop(getSelf());
            } else {
                if (!stack.isEmpty()) {
                    workerRouter.tell(new Merge(stack.pop().getList(), message.getList()), getSelf());
                } else {
                    stack.push(message);
                }
            }
        }

    }

    public static class Worker extends UntypedActor {
        @Override
        public void onReceive(Object message) throws Exception {
            if (message instanceof Merge) {
                Merge merge = (Merge) message;
                List<Integer> result = merge(merge.getLeft(), merge.getRight());
                getSender().tell(new Merged(result), getSelf());
            } else {
                unhandled(message);
            }
        }

        private static List<Integer> merge(List<Integer> left, List<Integer> right) {
            left = new ArrayList<>(left);
            right = new ArrayList<>(right);
            List<Integer> result = new ArrayList<>();

            while (left.size() > 0 && right.size() > 0) {
                if (left.get(0) <= right.get(0)) {
                    result.add(left.get(0));
                    left.remove(0);
                } else {
                    result.add(right.get(0));
                    right.remove(0);
                }
            }

            if (left.size() != 0) {
                for (Integer number : left) {
                    result.add(number);
                }
            } else {
                for (Integer number : right) {
                    result.add(number);
                }
            }
            return result;
        }

    }

    private static class MergeSort {
        private final List<Integer> work;

        public MergeSort(Iterable<Integer> work) {
            this.work = ImmutableList.copyOf(work);
        }

        public List<Integer> getWork() {
            return ImmutableList.copyOf(work);
        }
    }

    private static class Merge {
        final List<Integer> left;
        final List<Integer> right;

        public Merge(List<Integer> left, List<Integer> right) {
            this.left = ImmutableList.copyOf(left);
            this.right = ImmutableList.copyOf(right);
        }

        public List<Integer> getRight() {
            return ImmutableList.copyOf(right);
        }

        public List<Integer> getLeft() {
            return ImmutableList.copyOf(left);
        }

    }

    private static class Merged {
        final List<Integer> sortedList;

        public Merged(List<Integer> sortedList) {
            this.sortedList = ImmutableList.copyOf(sortedList);
        }

        public List<Integer> getList() {
            return ImmutableList.copyOf(sortedList);
        }

    }

    private static class Sorted {
        final List<Integer> sortedList;
        final Duration duration;

        public Sorted(List<Integer> sortedList, Duration duration) {
            this.sortedList = ImmutableList.copyOf(sortedList);
            this.duration = duration;
        }
    }

}
