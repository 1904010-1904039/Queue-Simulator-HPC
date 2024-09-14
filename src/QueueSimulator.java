// import java.util.Random;
// import java.util.concurrent.*;
// import java.util.concurrent.atomic.*;

// public class QueueSimulator {
//     private final long simulationTime;
//     private final BankQueue bankQueue;
//     private final GroceryQueues groceryQueues;
//     private final AtomicInteger bankCustomersArrived = new AtomicInteger(0);
//     private final AtomicInteger bankCustomersServed = new AtomicInteger(0);
//     private final AtomicLong bankTotalServiceTime = new AtomicLong(0);
//     private final AtomicInteger groceryCustomersArrived = new AtomicInteger(0);
//     private final AtomicInteger groceryCustomersServed = new AtomicInteger(0);
//     private final AtomicLong groceryTotalServiceTime = new AtomicLong(0);

//     public QueueSimulator(long simulationTime, int bankTellers, int bankMaxQueue, int groceryCashiers, int groceryMaxQueue) {
//         this.simulationTime = simulationTime;
//         this.bankQueue = new BankQueue(bankTellers, bankMaxQueue);
//         this.groceryQueues = new GroceryQueues(groceryCashiers, groceryMaxQueue);
//     }

//     public void simulate() {
//         long startTime = System.currentTimeMillis();
//         long endTime = startTime + (simulationTime * 60 * 1000);

//         ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
//         executor.scheduleAtFixedRate(this::generateCustomer, 0, 1, TimeUnit.SECONDS);
//         executor.scheduleAtFixedRate(this::processQueues, 0, 1, TimeUnit.SECONDS);

//         while (System.currentTimeMillis() < endTime) {
//             try {
//                 Thread.sleep(1000);
//             } catch (InterruptedException e) {
//                 Thread.currentThread().interrupt();
//                 break;
//             }
//         }

//         executor.shutdownNow();
//         printResults();
//     }

//     private void generateCustomer() {
//         Customer customer = new Customer(System.currentTimeMillis(), new Random().nextInt(5) + 1);
        
//         if (bankQueue.addCustomer(customer)) {
//             bankCustomersArrived.incrementAndGet();
//         }

//         if (groceryQueues.addCustomer(customer)) {
//             groceryCustomersArrived.incrementAndGet();
//         }
//     }

//     private void processQueues() {
//         bankQueue.processCustomers(bankCustomersServed, bankTotalServiceTime);
//         groceryQueues.processCustomers(groceryCustomersServed, groceryTotalServiceTime);
//     }

//     private void printResults() {
//         System.out.println("Bank Queue Results:");
//         System.out.println("Total customers arrived: " + bankCustomersArrived.get());
//         System.out.println("Total customers served: " + bankCustomersServed.get());
//         System.out.println("Total customers who left without being served: " + (bankCustomersArrived.get() - bankCustomersServed.get()));
//         System.out.println("Average total time: " + (bankCustomersServed.get() > 0 ? bankTotalServiceTime.get() / bankCustomersServed.get() / 1000.0 : 0) + " seconds");

//         System.out.println("\nGrocery Queues Results:");
//         System.out.println("Total customers arrived: " + groceryCustomersArrived.get());
//         System.out.println("Total customers served: " + groceryCustomersServed.get());
//         System.out.println("Total customers who left without being served: " + (groceryCustomersArrived.get() - groceryCustomersServed.get()));
//         System.out.println("Average total time: " + (groceryCustomersServed.get() > 0 ? groceryTotalServiceTime.get() / groceryCustomersServed.get() / 1000.0 : 0) + " seconds");
//     }

//     public static void main(String[] args) {
//         QueueSimulator simulator = new QueueSimulator(10, 3, 5, 3, 2);
//         simulator.simulate();
//     }
// }

import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class QueueSimulator {
    private final long simulationTime;
    private final BankQueue bankQueue;
    private final GroceryQueues groceryQueues;
    private final AtomicInteger bankCustomersArrived = new AtomicInteger(0);
    private final AtomicInteger bankCustomersServed = new AtomicInteger(0);
    private final AtomicLong bankTotalServiceTime = new AtomicLong(0);
    private final AtomicInteger groceryCustomersArrived = new AtomicInteger(0);
    private final AtomicInteger groceryCustomersServed = new AtomicInteger(0);
    private final AtomicLong groceryTotalServiceTime = new AtomicLong(0);

    public QueueSimulator(long simulationTime, int bankTellers, int bankMaxQueue, int groceryCashiers, int groceryMaxQueue) {
        this.simulationTime = simulationTime;
        this.bankQueue = new BankQueue(bankTellers, bankMaxQueue);
        this.groceryQueues = new GroceryQueues(groceryCashiers, groceryMaxQueue);
    }

    public void simulate() {
        long startTime = System.currentTimeMillis();
        long endTime = startTime + (simulationTime * 60 * 1000); // Convert minutes to milliseconds

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
        executor.scheduleAtFixedRate(this::generateCustomer, 0, 1, TimeUnit.SECONDS);
        executor.scheduleAtFixedRate(this::processQueues, 0, 1, TimeUnit.SECONDS);

        while (System.currentTimeMillis() < endTime) {
            try {
                Thread.sleep(1000); // Sleep for 1 second between each iteration
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        // Gracefully shutdown the executor after the simulation time
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }

        // After the simulation finishes, print the results
        printResults();
    }

    private void generateCustomer() {
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

    private void processQueues() {
        // Process customers in both bank and grocery queues
        bankQueue.processCustomers(bankCustomersServed, bankTotalServiceTime);
        groceryQueues.processCustomers(groceryCustomersServed, groceryTotalServiceTime);
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
        // Set up simulation parameters: 5 minutes of simulation, 3 tellers, max queue length of 10 for the bank,
        // and 4 grocery cashiers with a max queue length of 5
        QueueSimulator simulator = new QueueSimulator(5, 3, 10, 4, 5);

        // Start the simulation
        simulator.simulate();
    }
}
