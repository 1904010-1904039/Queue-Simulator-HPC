import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BankQueue {
    private BlockingQueue<Customer> queue;
    private int tellers;
    private Lock lock;

    public BankQueue(int tellers, int maxQueueLength) {
        this.tellers = tellers;
        this.queue = new LinkedBlockingQueue<>(maxQueueLength);
        this.lock = new ReentrantLock();
    }

    public boolean addCustomer(Customer customer) {
        lock.lock();
        try {
            return queue.offer(customer);
        } finally {
            lock.unlock();
        }
    }

    public Customer serveCustomer() {
        lock.lock();
        try {
            return queue.poll();
        } finally {
            lock.unlock();
        }
    }

    public boolean isFull() {
        return queue.remainingCapacity() == 0;
    }
}
