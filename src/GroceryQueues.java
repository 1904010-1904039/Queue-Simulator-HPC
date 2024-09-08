import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Random;

public class GroceryQueues extends AbstractQueue {
    private final int numQueues;
    private final ArrayBlockingQueue<Customer>[] queues;
    private final Customer[] cashiers;
    private final Lock lock;
    private final Random random;

    @SuppressWarnings("unchecked")
    public GroceryQueues(int numQueues, int maxQueueLength) {
        this.numQueues = numQueues;
        this.queues = new ArrayBlockingQueue[numQueues];
        for (int i = 0; i < numQueues; i++) {
            this.queues[i] = new ArrayBlockingQueue<>(maxQueueLength);
        }
        this.cashiers = new Customer[numQueues];
        this.lock = new ReentrantLock();
        this.random = new Random();
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
            int minQueueSize = Integer.MAX_VALUE;
            int minQueueIndex = -1;
            for (int i = 0; i < numQueues; i++) {
                if (queues[i].size() < minQueueSize) {
                    minQueueSize = queues[i].size();
                    minQueueIndex = i;
                } else if (queues[i].size() == minQueueSize && random.nextBoolean()) {
                    minQueueIndex = i;
                }
            }

            if (queues[minQueueIndex].offer(customer)) {
                stats.put("totalCustomers", stats.get("totalCustomers") + 1);
                return true;
            } else {
                Instant startTime = Instant.now();
                while (Duration.between(startTime, Instant.now()).toSeconds() < 10) {
                    for (int i = 0; i < numQueues; i++) {
                        if (queues[i].offer(customer)) {
                            stats.put("totalCustomers", stats.get("totalCustomers") + 1);
                            return true;
                        }
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                customer.setNotServed();
                stats.put("customersLeft", stats.get("customersLeft") + 1);
                return false;
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void processCustomers(Instant currentTime) {
        lock.lock();
        try {
            for (int i = 0; i < numQueues; i++) {
                if (cashiers[i] == null && !queues[i].isEmpty()) {
                    cashiers[i] = queues[i].poll();
                    cashiers[i].startService(currentTime);
                } else if (cashiers[i] != null && 
                           Duration.between(cashiers[i].getStartServiceTime(), currentTime).compareTo(cashiers[i].getServiceTime()) >= 0) {
                    cashiers[i].endService(currentTime);
                    stats.put("customersServed", stats.get("customersServed") + 1);
                    stats.put("totalServiceTime", stats.get("totalServiceTime") + (int)cashiers[i].getTotalTime().toSeconds());
                    cashiers[i] = null;
                }
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Map<String, Integer> getStats() {
        return new HashMap<>(stats);
    }
}