import java.util.Random;

public class GroceryQueues {
    private GroceryQueue[] queues;
    private Random random;

    public GroceryQueues(int numQueues, int maxQueueLength) {
        this.queues = new GroceryQueue[numQueues];
        for (int i = 0; i < numQueues; i++) {
            queues[i] = new GroceryQueue(maxQueueLength);
        }
        this.random = new Random();
    }

    public boolean addCustomer(Customer customer) {
        GroceryQueue shortestQueue = null;
        for (GroceryQueue queue : queues) {
            if (!queue.isFull() && (shortestQueue == null || queue.size() < shortestQueue.size())) {
                shortestQueue = queue;
            }
        }
        if (shortestQueue != null) {
            return shortestQueue.addCustomer(customer);
        }
        return false;
    }

    public void serveCustomers() {
        for (GroceryQueue queue : queues) {
            queue.serveCustomer();
        }
    }

    public boolean allQueuesFull() {
        for (GroceryQueue queue : queues) {
            if (!queue.isFull()) {
                return false;
            }
        }
        return true;
    }
}
