import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class GroceryQueue {
    private BlockingQueue<Customer> queue;

    public GroceryQueue(int maxQueueLength) {
        this.queue = new LinkedBlockingQueue<>(maxQueueLength);
    }

    public boolean addCustomer(Customer customer) {
        return queue.offer(customer);
    }

    public Customer serveCustomer() {
        return queue.poll();
    }

    public boolean isFull() {
        return queue.remainingCapacity() == 0;
    }

    public int size() {
        return queue.size();
    }
}
