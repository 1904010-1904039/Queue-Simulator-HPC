import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class QueueSimulator {
    private BankQueue bankQueue;
    private GroceryQueues groceryQueues;
    private int simulationTime;
    private AtomicInteger totalCustomers;
    private AtomicInteger customersServed;
    private AtomicInteger customersLeft;
    private AtomicInteger totalServiceTime;
    private Random random;

    public QueueSimulator(int simulationTime) {
        this.simulationTime = simulationTime;
        this.bankQueue = new BankQueue(3, 5);
        this.groceryQueues = new GroceryQueues(3, 2);
        this.totalCustomers = new AtomicInteger();
        this.customersServed = new AtomicInteger();
        this.customersLeft = new AtomicInteger();
        this.totalServiceTime = new AtomicInteger();
        this.random = new Random();
    }

    public void simulate() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
        scheduler.scheduleAtFixedRate(this::addCustomer, 0, random.nextInt(41) + 20, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(this::serveCustomers, 0, 1, TimeUnit.SECONDS);

        try {
            Thread.sleep(simulationTime * 60 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        scheduler.shutdown();
        try {
            scheduler.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        printResults();
    }

    private void addCustomer() {

        // customer(arrival time, service time, bool served)
        Customer customer = new Customer(System.currentTimeMillis(), random.nextInt(241) + 60);

        // increment the total number of customer with atomic operation 
        totalCustomers.incrementAndGet();

        // adding to the 
        if (!bankQueue.addCustomer(customer)) {
            customersLeft.incrementAndGet();
        }
        if (!groceryQueues.addCustomer(customer)) {
            customersLeft.incrementAndGet();
        }
    }

    private void serveCustomers() {
        Customer customer = bankQueue.serveCustomer();
        if (customer != null) {
            customersServed.incrementAndGet();
            totalServiceTime.addAndGet((int) customer.getServiceTime());
        }
        groceryQueues.serveCustomers();
    }

    private void printResults() {
        System.out.println("Total customers: " + totalCustomers.get());
        System.out.println("Customers served: " + customersServed.get());
        System.out.println("Customers left: " + customersLeft.get());
        System.out.println("Average service time: " + (totalServiceTime.get() / (double) customersServed.get()));
    }

    // public static void main(String[] args) {
    //     if (args.length != 1) {
    //         System.out.println("Usage: java QueueSimulator <simulation time in minutes>");
    //         return;
    //     }
    //     int simulationTime = Integer.parseInt(args[0]);
    //     QueueSimulator simulator = new QueueSimulator(simulationTime);
    //     simulator.simulate();
    // }
}