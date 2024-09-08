import java.time.Instant;
import java.time.Duration;

public class Customer {
    private final Instant arrivalTime;
    private final Duration serviceTime;
    private Instant startServiceTime;
    private Instant endServiceTime;
    private boolean notServed;

    // Initialize a new customer
    public Customer(Instant arrivalTime, Duration serviceTime) {
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
        this.notServed = false;
    }

    // setting the start time
    public void startService(Instant startTime) {
        this.startServiceTime = startTime;
    }

    // ending time
    public void endService(Instant endTime) {
        this.endServiceTime = endTime;
    }

    // checking of served or not
    public void setNotServed() {
        this.notServed = true;
    }

    public boolean isNotServed() {
        return notServed;
    }

    // total sevice time for a customer
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