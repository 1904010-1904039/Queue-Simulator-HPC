import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QueueSimulator {
    private final AbstractQueue queue;
    private final int simulationTime;
    private int clock;
    private final List<Object[]> events;

    public QueueSimulator(AbstractQueue queue, int simulationTime) {
        this.queue = queue;
        this.simulationTime = simulationTime;
        this.clock = 0;
        this.events = new ArrayList<>();
    }

    public void addCustomer(Customer customer) {
        events.add(new Object[]{"arrival", customer, clock});
    }

    public void run() {
        while (clock < simulationTime) {
            processEvents();
            queue.processCustomers(Instant.now());
            clock++;
            try {
                Thread.sleep(10); // Slow down simulation for real-time observation
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void processEvents() {
        List<Object[]> processedEvents = new ArrayList<>();
        for (Object[] event : events) {
            String eventType = (String) event[0];
            Customer customer = (Customer) event[1];
            int eventTime = (int) event[2];
            if (eventTime == clock) {
                if ("arrival".equals(eventType)) {
                    queue.addCustomer(customer);
                }
                processedEvents.add(event);
            }
        }
        events.removeAll(processedEvents);
    }

    public Map<String, Integer> getStats() {
        Map<String, Integer> stats = queue.getStats();
        if (stats.get("customersServed") > 0) {
            stats.put("averageServiceTime", stats.get("totalServiceTime") / stats.get("customersServed"));
        } else {
            stats.put("averageServiceTime", 0);
        }
        return stats;
    }
}