import java.time.Instant;
import java.time.Duration;

public class Customer {
    private final Instant arrivalTime;
    private final Duration serviceTime;
    private Instant startServiceTime;
    private Instant endServiceTime;
    private boolean notServed;

    public Customer(Instant arrivalTime, Duration serviceTime) {
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
        this.notServed = false;
    }

    public void startService(Instant startTime) {
        this.startServiceTime = startTime;
    }

    public void endService(Instant endTime) {
        this.endServiceTime = endTime;
    }

    public void setNotServed() {
        this.notServed = true;
    }

    public Duration getTotalTime() {
        return endServiceTime != null ? Duration.between(arrivalTime, endServiceTime) : null;
    }

    public Instant getArrivalTime() {
        return arrivalTime;
    }

    public Duration getServiceTime() {
        return serviceTime;
    }

    public Instant getStartServiceTime() {
        return startServiceTime;
    }
}