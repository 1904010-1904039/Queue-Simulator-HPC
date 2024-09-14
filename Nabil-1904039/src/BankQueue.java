import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class BankQueue {
    private final BlockingQueue<Customer> queue;
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    public BankQueue(int tellers, int maxQueueLength) {
        this.queue = new ArrayBlockingQueue<>(maxQueueLength);

        // Start teller threads (1 thread per teller)
        for (int i = 0; i < tellers; i++) {
            executor.scheduleAtFixedRate(this::serveCustomer, 0, 1, TimeUnit.SECONDS);
        }
    }

    public boolean addCustomer(Customer customer) {
        return queue.offer(customer);
    }

    private void serveCustomer() {
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
        while (!queue.isEmpty()) {
            Customer customer = queue.poll();
            if (customer != null) {
                customersServed.incrementAndGet();
                totalServiceTime.addAndGet(customer.getServiceTime());
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
