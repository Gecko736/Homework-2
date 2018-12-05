import java.util.concurrent.Semaphore;

public class Main {
    public static Factory factory;

    public static Semaphore next = new Semaphore(1);

    public static void main(String[] args) {
        System.out.println("Producers | Consumers | InterArrival | Service | Queue | Packets | " +
                "Discarded | Utilization | Throughput | Avg Service | Max Service | Avg Wait | Max Wait | " +
                "Avg Turnaround | Max Turnaround | Avg Offer | Max Offer");
        System.out.println("----------+-----------+--------------+---------+-------+---------+-" +
                "----------+-------------+------------+-------------+-------------+----------+----------+-" +
                "---------------+----------------+-----------+-----------");
        for (int producers = 1; producers < 3; producers++) {
            int interArrivalTime = producers == 1 ? 50 : 120;
            for (int consumers = 1; consumers < 3; consumers++) {
                int serviceTime = consumers == 1 ? 50 : 120;
                for (int queueLength = 3; queueLength <= 10; queueLength++) {
                    try {
                        next.acquire();
                        System.out.printf("%-9d   %-9d   %-12d   %-7d   %-5d   5000    | ",
                                producers, consumers, interArrivalTime, serviceTime, queueLength);
                        factory = new Factory(producers, consumers, interArrivalTime,
                                serviceTime, true, queueLength, 5000);
                        factory.start();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        }
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
