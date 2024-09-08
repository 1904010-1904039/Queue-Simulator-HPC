import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BankQueue extends AbstractQueue {
    private final int numTellers;
    private final ArrayBlockingQueue<Customer> queue;
    private final Customer[] tellers;
    private final Lock lock;

    public BankQueue(int numTellers, int maxQueueLength) {
        this.numTellers = numTellers;
        this.queue = new ArrayBlockingQueue<>(maxQueueLength);
        this.tellers = new Customer[numTellers];
        this.lock = new ReentrantLock();
        this.stats = new HashMap<>();
        stats.put("totalCustomers", 0);
        stats.put("customersServed", 0);
        stats.put("customersLeft", 0);
        stats.put("totalServiceTime", 0);
    }

    @Override
    public boolean addCustomer(Customer customer) {
        lock.lock();
        try {
            if (queue.offer(customer)) {
                stats.put("totalCustomers", stats.get("totalCustomers") + 1);
                return true; //served
            } else {
                customer.setNotServed();
                stats.put("customersLeft", stats.get("customersLeft") + 1);
                return false; // if not served
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void processCustomers(Instant currentTime) {
        lock.lock();
        try {
            // for all the tellers
            for (int i = 0; i < numTellers; i++) {
                
                // if teller is idle and queue not empty
                if (tellers[i] == null && !queue.isEmpty()) {
                    tellers[i] = queue.poll(); // assings next customer from the queue to the teller
                    tellers[i].startService(currentTime); // Starts the customer service
                } else if (tellers[i] != null && 
                           Duration.between(tellers[i].getStartServiceTime(), currentTime).compareTo(tellers[i].getServiceTime()) >= 0) { // if the teller is not idle and customer service time has expired then,
                    // customer is served
                    tellers[i].endService(currentTime);

                    // updating the stats
                    stats.put("customersServed", stats.get("customersServed") + 1);
                    stats.put("totalServiceTime", stats.get("totalServiceTime") + (int)tellers[i].getTotalTime().toSeconds());

                    // making the teller[i] available again
                    tellers[i] = null;
                }
            }
        } finally {
            lock.unlock();
        }
    }

    // return a Defensive Copy of the stats
    // This ensures that the returned map is a separate object from the internal stats map. 
    @Override
    public Map<String, Integer> getStats() {
        return new HashMap<>(stats);
    }
}