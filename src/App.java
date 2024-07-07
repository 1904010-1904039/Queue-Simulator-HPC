public class App {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java QueueSimulator <simulation time in minutes>");
            return;
        }
        int simulationTime = Integer.parseInt(args[0]);
        QueueSimulator simulator = new QueueSimulator(simulationTime);
        simulator.simulate();
    }
}
