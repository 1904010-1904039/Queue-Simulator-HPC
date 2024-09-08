import java.time.Instant;
import java.util.Map;

public abstract class AbstractQueue {
    protected Map<String, Integer> stats;

    public abstract boolean addCustomer(Customer customer);
    public abstract void processCustomers(Instant currentTime);
    public abstract Map<String, Integer> getStats();
}