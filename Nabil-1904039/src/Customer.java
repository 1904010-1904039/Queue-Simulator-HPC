public class Customer {
    private final long arrivalTime;
    private final int serviceTime;

    public Customer(long arrivalTime, int serviceTime) {
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
    }

    public int getServiceTime() {
        return serviceTime;
    }

    public long getArrivalTime() {
        return arrivalTime;
    }
}