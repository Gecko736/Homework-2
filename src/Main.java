public class Main {
    public static Factory factory;

    public static void main(String[] args) {
        factory = new Factory(1, 50, 80,
                false, 5, 1, 1000);
        factory.start();
    }

    public static final String HEADER =
            "  PID |  Conceived |    Created |     Needed |    Arrived |      Ended | Turnaround |     Waited";
    public static final String BAR =
            "------+------------+------------+------------+------------+------------+------------+------------";

    public static void printStats(int PID, long interArrivalTime, long creationTime, long serviceTime,
                                  long arrivalTime, long endTime, long turnaroundTime, long waitTime) {
        System.out.printf("%5d | %10d | %10d | %10d | %10d | %10d | %10d | %10d\n",
                PID, interArrivalTime, creationTime, serviceTime, arrivalTime, endTime, turnaroundTime, waitTime);
    }

    public static void printStats(int PID, long interArrivalTime, long creationTime, long serviceTime, long endTime) {
        System.out.printf("%5d | %10d | %10d | %10d | ----NA---- | %10d | ----NA---- | ----NA----\n",
                PID, interArrivalTime, creationTime, serviceTime, endTime);
    }
}
