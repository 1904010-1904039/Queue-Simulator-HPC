public class Customer {
    private final long arrivalTime;
    private final int serviceTime;
    private long startServiceTime;
    private long endServiceTime;
    private boolean served;

    public Customer(long arrivalTime, int serviceTime) {
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
        this.served = false;
    }

    public long getArrivalTime() { return arrivalTime; }
    public int getServiceTime() { return serviceTime; }
    public boolean isServed() { return served; }
    public void setServed(boolean served) { this.served = served; }

    public void setStartServiceTime(long startServiceTime) {
        this.startServiceTime = startServiceTime;
    }

    public void setEndServiceTime(long endServiceTime) {
        this.endServiceTime = endServiceTime;
    }

    public long getStartServiceTime() { return startServiceTime; }
    public long getEndServiceTime() { return endServiceTime; }

    public long getWaitingTime() {
        return startServiceTime - arrivalTime;
    }

    public long getTotalTime() {
        return endServiceTime - arrivalTime;
    }
}