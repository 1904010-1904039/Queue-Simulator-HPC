import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class GroceryQueues {
    private final List<BlockingQueue<Customer>> queues;
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    public GroceryQueues(int cashiers, int maxQueueLength) {
        this.queues = new ArrayList<>();
        for (int i = 0; i < cashiers; i++) {
            queues.add(new ArrayBlockingQueue<>(maxQueueLength));
        }

        // Start cashier threads (1 thread per cashier)
        for (int i = 0; i < cashiers; i++) {
            final int cashierIndex = i; // Make i final for use in lambda expression
            executor.scheduleAtFixedRate(() -> serveCustomer(cashierIndex), 0, 1, TimeUnit.SECONDS);
        }
    }

    public boolean addCustomer(Customer customer) {
        BlockingQueue<Customer> minQueue = queues.stream()
                .min(Comparator.comparingInt(BlockingQueue::size))
                .orElse(null);
        if (minQueue != null && minQueue.remainingCapacity() > 0) {
            return minQueue.offer(customer);
        }
        return false;
    }

    private void serveCustomer(int index) {
        BlockingQueue<Customer> queue = queues.get(index);
        Customer customer = queue.poll();
        if (customer != null) {
            try {
                TimeUnit.SECONDS.sleep(customer.getServiceTime());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void processCustomers(AtomicInteger customersServed, AtomicLong totalServiceTime) {
        for (BlockingQueue<Customer> queue : queues) {
            while (!queue.isEmpty()) {
                Customer customer = queue.poll();
                if (customer != null) {
                    customersServed.incrementAndGet();
                    totalServiceTime.addAndGet(customer.getServiceTime());
                }
            }
        }
    }

    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
}
