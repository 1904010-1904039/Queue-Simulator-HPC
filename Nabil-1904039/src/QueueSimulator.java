import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class QueueSimulator {
    private final long simulationTime; // in minutes
    private final BankQueue bankQueue;
    private final GroceryQueues groceryQueues;
    private final AtomicInteger bankCustomersArrived = new AtomicInteger(0);
    private final AtomicInteger bankCustomersServed = new AtomicInteger(0);
    private final AtomicLong bankTotalServiceTime = new AtomicLong(0);
    private final AtomicInteger groceryCustomersArrived = new AtomicInteger(0);
    private final AtomicInteger groceryCustomersServed = new AtomicInteger(0);
    private final AtomicLong groceryTotalServiceTime = new AtomicLong(0);
    private volatile boolean running = true;
    private final Random random = new Random();

    public QueueSimulator(long simulationTime, int bankTellers, int bankMaxQueue, int groceryCashiers, int groceryMaxQueue) {
        this.simulationTime = simulationTime; // Minutes of simulation time
        this.bankQueue = new BankQueue(bankTellers, bankMaxQueue);
        this.groceryQueues = new GroceryQueues(groceryCashiers, groceryMaxQueue);
    }

    public void simulate() {
        long startTime = System.currentTimeMillis();
        long endTime = startTime + (simulationTime * 60 * 1000); // Convert minutes to milliseconds

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
        ScheduledFuture<?> customerGenerator = null;
        ScheduledFuture<?> queueProcessor = null;

        try {
            // Generate customers at random intervals
            customerGenerator = executor.scheduleAtFixedRate(this::generateCustomer, 0, random.nextInt(41) + 20, TimeUnit.SECONDS);

            // Process queues every second
            queueProcessor = executor.scheduleAtFixedRate(this::processQueues, 0, 1, TimeUnit.SECONDS);

            // Run for the simulation time
            while (System.currentTimeMillis() < endTime && running) {
                try {
                    Thread.sleep(1000); // Sleep for 1 second
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        } finally {
            // Stop generating customers and processing queues
            if (customerGenerator != null) customerGenerator.cancel(true);
            if (queueProcessor != null) queueProcessor.cancel(true);

            // Shutdown the queues
            shutdownBankQueue();
            shutdownGroceryQueues();

            // Shutdown executor
            executor.shutdown();
            try {
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }

            // Print results
            printResults();
        }
    }

    private void generateCustomer() {
        if (!running) return;

        int serviceTimeBank = random.nextInt(241) + 60; // Random service time between 60 and 300 seconds
        int serviceTimeGrocery = random.nextInt(241) + 60;

        Customer bankCustomer = new Customer(System.currentTimeMillis(), serviceTimeBank);
        Customer groceryCustomer = new Customer(System.currentTimeMillis(), serviceTimeGrocery);

        if (bankQueue.addCustomer(bankCustomer)) {
            bankCustomersArrived.incrementAndGet();
        }

        if (groceryQueues.addCustomer(groceryCustomer)) {
            groceryCustomersArrived.incrementAndGet();
        }
    }

    private void processQueues() {
        if (!running) return;

        bankQueue.processCustomers(bankCustomersServed, bankTotalServiceTime);
        groceryQueues.processCustomers(groceryCustomersServed, groceryTotalServiceTime);
    }

    public void shutdownBankQueue() {
        if (bankQueue != null) bankQueue.shutdown();
    }

    public void shutdownGroceryQueues() {
        if (groceryQueues != null) groceryQueues.shutdown();
    }

    private void printResults() {
        System.out.println("Bank Queue Results:");
        System.out.println("Customers Arrived: " + bankCustomersArrived.get());
        System.out.println("Customers Served: " + bankCustomersServed.get());
        System.out.println("Average Service Time (seconds): " +
            (bankCustomersServed.get() == 0 ? 0 : bankTotalServiceTime.get() / bankCustomersServed.get()));

        System.out.println("\nGrocery Queue Results:");
        System.out.println("Customers Arrived: " + groceryCustomersArrived.get());
        System.out.println("Customers Served: " + groceryCustomersServed.get());
        System.out.println("Average Service Time (seconds): " +
            (groceryCustomersServed.get() == 0 ? 0 : groceryTotalServiceTime.get() / groceryCustomersServed.get()));
    }

    public static void main(String[] args) {
        // Set up simulation parameters: 5 minutes of simulation, 3 tellers, max queue length of 5 for the bank,
        // and 3 grocery cashiers with a max queue length of 5
        QueueSimulator simulator = new QueueSimulator(2, 3, 5, 3, 5);

        // Start the simulation
        simulator.simulate();
    }
}
