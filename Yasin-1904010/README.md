# Queue Simulator Codebase Explanation

## 1. Customer Class

The `Customer` class represents an individual customer in the simulation.

```java
public class Customer {
    private final long arrivalTime;
    private final int serviceTime;
    private long startServiceTime;
    private long endServiceTime;
    private boolean served;

    // Constructor and methods...
}
```

- `arrivalTime`: When the customer arrived in the queue (timestamp).
- `serviceTime`: How long it takes to serve this customer (in seconds).
- `startServiceTime`: When the customer started being served.
- `endServiceTime`: When the customer finished being served.
- `served`: Boolean flag indicating if the customer has been served.

Methods like `getWaitingTime()` and `getTotalTime()` calculate derived times based on these timestamps.

## 2. BankQueue Class

The `BankQueue` class simulates a single queue with multiple tellers.

```java
public class BankQueue {
    private final int numTellers;
    private final int maxQueueLength;
    private final BlockingQueue<Customer> queue;
    private final Semaphore tellers;
    private final AtomicBoolean running;

    // Constructor and methods...
}
```

- Uses a `BlockingQueue` to manage customers.
- `Semaphore` controls access to tellers, ensuring only `numTellers` customers are served simultaneously.
- `addCustomer()`: Attempts to add a customer to the queue.
- `processCustomers()`: Simulates serving customers. It acquires a teller (semaphore), serves the customer, and releases the teller.
- `shutdown()`: Stops the queue processing.

## 3. GroceryQueues Class

The `GroceryQueues` class simulates multiple queues, each with its own cashier.

```java
public class GroceryQueues {
    private final int numQueues;
    private final int maxQueueLength;
    private final List<BlockingQueue<Customer>> queues;
    private final List<ReentrantLock> locks;
    private final AtomicBoolean running;
    private final ExecutorService executorService;

    // Constructor and methods...
}
```

- Manages multiple queues using a list of `BlockingQueue`s.
- Uses an `ExecutorService` to manage threads for each queue.
- `addCustomer()`: Adds a customer to the shortest queue.
- `processCustomers()`: Creates a task for each queue to continuously process customers.
- `shutdown()`: Stops all queue processing and shuts down the `ExecutorService`.

## 4. QueueSimulator Class

The `QueueSimulator` class orchestrates the entire simulation.

```java
public class QueueSimulator {
    private final long simulationTime;
    private final BankQueue bankQueue;
    private final GroceryQueues groceryQueues;
    private final AtomicInteger bankCustomersArrived;
    private final AtomicInteger bankCustomersServed;
    private final AtomicLong bankTotalServiceTime;
    private final AtomicInteger groceryCustomersArrived;
    private final AtomicInteger groceryCustomersServed;
    private final AtomicLong groceryTotalServiceTime;
    private final AtomicBoolean running;
    private ScheduledExecutorService executor;

    // Constructor and methods...
}
```

- Manages both `BankQueue` and `GroceryQueues` instances.
- Uses `AtomicInteger` and `AtomicLong` for thread-safe counting of customers and service times.
- `simulate()`: Runs the entire simulation:
  1. Sets up a `ScheduledExecutorService` to generate customers and process the bank queue at regular intervals.
  2. Starts the grocery queues processing.
  3. Runs for the specified simulation time.
  4. Shuts down all processes and prints results.
- `generateCustomer()`: Creates new customers and adds them to both queue types.
- `printResults()`: Outputs the simulation results.

## Key Concurrency Concepts Used

1. **Thread Safety**: 
   - Use of `AtomicInteger` and `AtomicLong` for safe concurrent updates to counters.
   - `AtomicBoolean` for managing the running state of queues.

2. **Synchronization**:
   - `Semaphore` in `BankQueue` to manage teller availability.
   - `ReentrantLock` in `GroceryQueues` to protect individual queue operations.

3. **Concurrent Collections**:
   - `BlockingQueue` for thread-safe queue operations.

4. **Thread Management**:
   - `ScheduledExecutorService` in `QueueSimulator` for scheduled tasks.
   - `ExecutorService` in `GroceryQueues` for managing cashier threads.

5. **Graceful Shutdown**:
   - Proper shutdown procedures for all executor services and queues.

This simulation provides a comprehensive example of managing concurrent processes in a complex system, demonstrating various Java concurrency utilities and patterns.
