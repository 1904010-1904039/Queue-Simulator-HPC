// import java.time.Duration;
// import java.time.Instant;
// import java.util.Map;
// import java.util.Random;

// public class Main {
//     public static void main(String[] args) {
//         int simulationTime = 2 * 60; // 2 hours in seconds

//         System.out.println("Bank Queue Simulation:");
//         BankQueue bankQueue = new BankQueue(3, 5);
//         Map<String, Double> bankStats = runSimulation(bankQueue, simulationTime);
//         printStats(bankStats);

//         System.out.println("\nGrocery Queues Simulation:");
//         GroceryQueues groceryQueues = new GroceryQueues(3, 2);
//         Map<String, Double> groceryStats = runSimulation(groceryQueues, simulationTime);
//         printStats(groceryStats);
//     }

//     private static Map<String, Double> runSimulation(AbstractQueue queueType, int simulationTime) {
//         QueueSimulator simulator = new QueueSimulator(queueType, simulationTime);
//         Random random = new Random();
    
//         for (int i = 0; i < simulationTime; i++) {
//             if (random.nextDouble() < 0.1) { // 10% chance of customer arrival each second
//                 int serviceTime = 10 + random.nextInt(10); // Service time between 10-19 seconds
//                 Customer customer = new Customer(Instant.now(), Duration.ofSeconds(serviceTime));
//                 simulator.addCustomer(customer);
//             }
            
//             // Process customers every second
//             simulator.processCustomers(Instant.ofEpochSecond(i));
            
//             try {
//                 Thread.sleep(1); // Simulate 1 second passing
//             } catch (InterruptedException e) {
//                 Thread.currentThread().interrupt();
//             }
//         }
    
//         return simulator.getStats();
//     }

//     private static void printStats(Map<String, Double> stats) {
//         System.out.println("Simulation Statistics:");
//         System.out.printf("Total customers: %.0f%n", stats.getOrDefault("totalCustomers", 0.0));
//         System.out.printf("Customers served: %.0f%n", stats.getOrDefault("customersServed", 0.0));
//         System.out.printf("Customers left without service: %.0f%n", stats.getOrDefault("customersLeft", 0.0));
//         System.out.printf("Customers not served (queue full): %.0f%n", stats.getOrDefault("customersNotServed", 0.0));
//         System.out.printf("Average service time: %.2f seconds%n", stats.getOrDefault("avgServiceTime", 0.0));
//         System.out.printf("Average waiting time: %.2f seconds%n", stats.getOrDefault("avgWaitingTime", 0.0));
//         System.out.printf("Maximum queue length: %.0f%n", stats.getOrDefault("maxQueueLength", 0.0));
//         System.out.printf("Average queue length: %.2f%n", stats.getOrDefault("avgQueueLength", 0.0));
//         System.out.printf("Arrival rate: %.2f customers/minute%n", stats.getOrDefault("arrivalRate", 0.0) * 60);
//         System.out.printf("Service rate: %.2f customers/minute%n", stats.getOrDefault("serviceRate", 0.0) * 60);
//         System.out.printf("Utilization: %.2f%%%n", stats.getOrDefault("utilization", 0.0) * 100);
        
//         double arrivalRate = stats.getOrDefault("arrivalRate", 0.0);
//         double serviceRate = stats.getOrDefault("serviceRate", 0.0);
//         double simulationTime = stats.getOrDefault("simulationTime", 1.0);
        
//         if (serviceRate > 0 && serviceRate >= arrivalRate) {
//             double rho = arrivalRate / serviceRate;
//             double avgCustomersInSystem = rho / (1 - rho);
//             double avgWaitingTime = avgCustomersInSystem / arrivalRate;
            
//             System.out.println("\nAdditional Queue Metrics (M/M/1 model approximation):");
//             System.out.printf("Traffic intensity (ρ): %.2f%n", rho);
//             System.out.printf("Average number of customers in the system: %.2f%n", avgCustomersInSystem);
//             System.out.printf("Theoretical average waiting time: %.2f seconds%n", avgWaitingTime);
//         } else if (serviceRate > 0) {
//             System.out.println("\nWarning: Arrival rate exceeds service rate. The queue may be unstable.");
//         } else {
//             System.out.println("\nWarning: Service rate is zero. Unable to calculate queue metrics.");
//         }
        
//         System.out.printf("Simulation time: %.0f seconds%n", simulationTime);
//     }
// }