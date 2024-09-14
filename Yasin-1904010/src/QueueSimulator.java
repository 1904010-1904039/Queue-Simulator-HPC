import java.util.Random;
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
    private final AtomicBoolean running = new AtomicBoolean(true);


    private ScheduledExecutorService executor;

    public QueueSimulator(long simulationTime, int bankTellers, int bankMaxQueue, int groceryCashiers, int groceryMaxQueue) {
        this.simulationTime = simulationTime; // Minutes of simulation time
        this.bankQueue = new BankQueue(bankTellers, bankMaxQueue);
        this.groceryQueues = new GroceryQueues(groceryCashiers, groceryMaxQueue);
    }

    public void simulate() {
        long startTime = System.currentTimeMillis();
        long endTime = startTime + (simulationTime * 60 * 1000); // Convert minutes to milliseconds

        //ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);
        executor = Executors.newScheduledThreadPool(3);

        // Generate customers every second
        ScheduledFuture<?> customerGenerator = executor.scheduleAtFixedRate(this::generateCustomer, 0, 1, TimeUnit.SECONDS);

        // Process queues every second
        // ScheduledFuture<?> queueProcessor = executor.scheduleAtFixedRate(this::processQueues, 0, 1, TimeUnit.SECONDS);


        // ScheduledFuture<?> bankQueueProcessor = executor.scheduleAtFixedRate(() -> bankQueue.processCustomers(bankCustomersServed, bankTotalServiceTime), 0, 1, TimeUnit.SECONDS);
        // ScheduledFuture<?> groceryQueueProcessor = executor.scheduleAtFixedRate(() -> groceryQueues.processCustomers(groceryCustomersServed, groceryTotalServiceTime), 0, 1, TimeUnit.SECONDS); 

        // Process bank and grocery queues every second
        ScheduledFuture<?> bankQueueProcessor = executor.scheduleAtFixedRate(() -> {
            if (running.get()) {
                bankQueue.processCustomers(bankCustomersServed, bankTotalServiceTime);
            }
        }, 0, 1, TimeUnit.SECONDS);

        // ScheduledFuture<?> groceryQueueProcessor = executor.scheduleAtFixedRate(() -> {
        //     if (running.get()) {
        //         groceryQueues.processCustomers(groceryCustomersServed, groceryTotalServiceTime);
        //     }
        // }, 0, 1, TimeUnit.SECONDS);

        groceryQueues.processCustomers(groceryCustomersServed, groceryTotalServiceTime);


        // Let the simulation run for the specified time
        while (System.currentTimeMillis() < endTime && running.get()) {
            try {
                Thread.sleep(1000); // Sleep for 1 second between each iteration
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        // Stop running the simulation
        running.set(false);

        // Stop generating customers and processing queues
        customerGenerator.cancel(true);
        bankQueueProcessor.cancel(true);
        //groceryQueueProcessor.cancel(true);

        // Shutdown the bank and grocery queues explicitly
        shutdownBankQueue();
        shutdownGroceryQueues();

        // Gracefully shut down the executor after the simulation
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                System.out.println("Time out over. Shutting down now");
                executor.shutdownNow();
            }
            else {
                System.out.println("Simulation completed successfully");
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }

        // After the simulation finishes, print the results
        printResults();
    }

    // Add a method to shutdown the BankQueue simulation
    public void shutdownBankQueue() {
        if (bankQueue != null) {
            bankQueue.shutdown();
        }
    }

    // Add a method to shutdown the GroceryQueues simulation
    public void shutdownGroceryQueues() {
        if (groceryQueues != null) {
            groceryQueues.shutdown();
        }
    }

    private void generateCustomer() {
        if (!running.get()) return;

        Customer bankCustomer = new Customer(System.currentTimeMillis(), new Random().nextInt(5) + 1);  // Random service time between 1 and 5 seconds
        Customer groceryCustomer = new Customer(System.currentTimeMillis(), new Random().nextInt(5) + 1);

        // Attempt to add customers to both queues
        if (bankQueue.addCustomer(bankCustomer)) {
            bankCustomersArrived.incrementAndGet();
        }

        if (groceryQueues.addCustomer(groceryCustomer)) {
            groceryCustomersArrived.incrementAndGet();
        }
    }

    private void printResults() {
        // Bank queue results
        System.out.println("Bank Queue Results:");
        System.out.println("Total customers arrived: " + bankCustomersArrived.get());
        System.out.println("Total customers served: " + bankCustomersServed.get());
        System.out.println("Average service time: " + (bankCustomersServed.get() == 0 ? 0 : bankTotalServiceTime.get() / bankCustomersServed.get()) + " ms");

        // Grocery queue results
        System.out.println("\nGrocery Queue Results:");
        System.out.println("Total customers arrived: " + groceryCustomersArrived.get());
        System.out.println("Total customers served: " + groceryCustomersServed.get());
        System.out.println("Average service time: " + (groceryCustomersServed.get() == 0 ? 0 : groceryTotalServiceTime.get() / groceryCustomersServed.get()) + " ms");
    }

    public static void main(String[] args) { 
        // Set up simulation parameters: 5 minutes of simulation, 3 tellers, max queue length of 5 for the bank,
        // and 4 grocery cashiers with a max queue length of 5
        QueueSimulator simulator = new QueueSimulator(2, 3, 5, 3, 5);

        // Start the simulation
        simulator.simulate();
    }
}
