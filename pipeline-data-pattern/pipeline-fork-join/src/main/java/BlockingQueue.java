import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by hongke on 1/12/15.
 */
public class BlockingQueue<T> {

    T[] items;
    private final ReentrantLock lock;
    private final Condition notEmpty;
    private final Condition notFull;

    /**
     * items index for next take, poll, peek or remove
     */
    int takeIndex;

    /**
     * items index for next put, offer, or add
     */
    int putIndex;

    /**
     * Number of elements in the queue
     */
    int count;

    public BlockingQueue(int capacity) {
        assert capacity > 0;
        items = (T[]) new Object[capacity];
        this.lock = new ReentrantLock();
        this.notEmpty = lock.newCondition();
        this.notFull = lock.newCondition();
    }

    public void put(T t) throws InterruptedException {
        assert t != null;
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            while (count == items.length) {
//                System.out.println("Blocking put");
                notFull.await();
            }
            insert(t);
        } finally {
            lock.unlock();
        }
    }

    public boolean offer(T t, long timeout, TimeUnit unit) throws InterruptedException {
        assert t != null;
        long nanos = unit.toNanos(timeout);
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            while (count == items.length) {
                if (nanos <= 0)
                    return false;
                nanos = notFull.awaitNanos(nanos);
            }
            insert(t);
            return true;
        } finally {
            lock.unlock();
        }
    }

    public T poll(long timeout, TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            while (count == 0) {
                if (nanos <= 0)
                    return null;
                nanos = notEmpty.awaitNanos(nanos);
            }
            return extract();
        } finally {
            lock.unlock();
        }
    }

    public T take() throws InterruptedException {
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            while (count == 0) {
//                System.out.println("Blocking take");
                notEmpty.await();
            }
            return extract();
        } finally {
            lock.unlock();
        }
    }


    /**
     * Inserts element at current put position, advances, and signals. Call only when holding lock.
     */
    private void insert(T t) {
        items[putIndex] = t;
        putIndex = inc(putIndex);
        ++count;
        notEmpty.signal();
    }


    /**
     * Extracts element at current take position, advances, and signals. Call only when holding lock.
     */
    private T extract() {
        final Object[] items = this.items;
        T t = this.<T>cast(items[takeIndex]);
        items[takeIndex] = null;
        takeIndex = inc(takeIndex);
        --count;
        notFull.signal();
        return t;
    }

    /**
     * Circularly increment i.
     */
    final int inc(int i) {
        return (++i == items.length) ? 0 : i;
    }

    @SuppressWarnings("unchecked")
    static <E> E cast(Object item) {
        return (E) item;
    }

}