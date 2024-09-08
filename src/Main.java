import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        int simulationTime = 120 * 60; // 2 hours in seconds

        System.out.println("Bank Queue Simulation:");
        BankQueue bankQueue = new BankQueue(3, 5);
        Map<String, Integer> bankStats = runSimulation(bankQueue, simulationTime);
        printStats(bankStats);

        System.out.println("\nGrocery Queues Simulation:");
        GroceryQueues groceryQueues = new GroceryQueues(3, 2);
        Map<String, Integer> groceryStats = runSimulation(groceryQueues, simulationTime);
        printStats(groceryStats);
    }

    private static Map<String, Integer> runSimulation(AbstractQueue queueType, int simulationTime) {
        QueueSimulator simulator = new QueueSimulator(queueType, simulationTime);
        Random random = new Random();

        for (int i = 0; i < simulationTime; i++) {
            if (random.nextDouble() < 0.1) { // 10% chance of customer arrival each second
                int serviceTime = 60 + random.nextInt(241); // Service time between 1-5 minutes
                Customer customer = new Customer(Instant.now(), Duration.ofSeconds(serviceTime));
                simulator.addCustomer(customer);
            }
        }

        simulator.run();
        return simulator.getStats();
    }

    private static void printStats(Map<String, Integer> stats) {
        System.out.println("Total customers: " + stats.get("totalCustomers"));
        System.out.println("Customers served: " + stats.get("customersServed"));
        System.out.println("Customers left: " + stats.get("customersLeft"));
        System.out.println("Average service time: " + stats.get("averageServiceTime") + " seconds");
    }
}