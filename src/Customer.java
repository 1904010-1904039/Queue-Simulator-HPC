public class Customer {
    private long arrivalTime;
    private int serviceTime;
    private boolean served;

    // Constructor
    public Customer(long l, int serviceTime) {
        this.arrivalTime = l;
        this.serviceTime = serviceTime;
        this.served = false;
    }

    public long getArrivalTime() {
        return arrivalTime;
    }

    public int getServiceTime() {
        return serviceTime;
    }

    public boolean isServed() {
        return served;
    }

    public void setServed(boolean served) {
        this.served = served;
    }
}
