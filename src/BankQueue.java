import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class BankQueue {
    private final int numTellers;
    private final int maxQueueLength;
    private final BlockingQueue<Customer> queue;
    private final Semaphore tellers;

    public BankQueue(int numTellers, int maxQueueLength) {
        this.numTellers = numTellers;
        this.maxQueueLength = maxQueueLength;
        this.queue = new LinkedBlockingQueue<>(maxQueueLength);
        this.tellers = new Semaphore(numTellers);
    }

    public boolean addCustomer(Customer customer) {
        return queue.offer(customer);
    }

    public void processCustomers(AtomicInteger customersServed, AtomicLong totalServiceTime) {
        try {
            tellers.acquire();
            Customer customer = queue.take();
            customer.setStartServiceTime(System.currentTimeMillis());
            customer.setServed(true);
            Thread.sleep(customer.getServiceTime() * 1000L);
            customer.setEndServiceTime(System.currentTimeMillis());
            customersServed.incrementAndGet();
            totalServiceTime.addAndGet(customer.getTotalTime());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            tellers.release();
        }
    }
}