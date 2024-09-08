import java.time.Instant;
import java.util.Map;

public abstract class AbstractQueue {
    // stores the statistics about the queue
    protected Map<String, Integer> stats;

    // adds a customer to the queue and returns if successfully added or not
    public abstract boolean addCustomer(Customer customer);
    
    public abstract void processCustomers(Instant currentTime);
    public abstract Map<String, Integer> getStats();
}