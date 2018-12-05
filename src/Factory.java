import java.math.BigInteger;
import java.util.PriorityQueue;
import java.util.concurrent.Semaphore;

public class Factory {
    private Producer[] networks;
    private BoundedBuffer receiveQueue;
    private Consumer[] processors;
    private PriorityQueue<Packet> trash;
    private final int trashLimit;
    private final Semaphore trashProtector = new Semaphore(1);
    public final boolean randomize;

    public Factory(int networks, long interArrivalTime, long serviceTime,
                   boolean randomize, int queueLength, int processors, int trashLimit) {
        receiveQueue = new BoundedBuffer(queueLength);
        trash = new PriorityQueue<>();
        this.trashLimit = trashLimit;

        this.networks = new Producer[networks];
        for (int i = 0; i < networks; i++)
            this.networks[i] = new Producer(interArrivalTime, serviceTime);

        this.processors = new Consumer[processors];
        for (int i = 0; i < processors; i++)
            this.processors[i] = new Consumer();

        this.randomize = randomize;
    }

    public void start() {
        for (Consumer c : processors)
            c.start();
        for (Producer p : networks)
            p.start();
    }

    private final BigInteger startTime = BigInteger.valueOf(System.currentTimeMillis());

    public long now() {
        return BigInteger.valueOf(System.currentTimeMillis()).subtract(startTime).longValue();
    }

    public void throwAway(Packet packet) {
        try {
            trashProtector.acquire();
            trash.add(packet);
            trashProtector.release();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    public void queue(Packet packet) {
        try {
            receiveQueue.add(packet);
        } catch (IllegalStateException e) {
            trash.add(packet);
        }
    }

    public Packet poll() {
        return receiveQueue.poll();
    }

    public boolean check() {
        if (trash.size() >= trashLimit) {
            long time = now();
            Thread.currentThread().getThreadGroup().interrupt();
            printAll(time);
            return false;
        }
        return true;
    }

    private static boolean printed = false;

    private synchronized void printAll(long totalTime) {
        if (!printed) {
            printed = true;

            System.out.println("Simulation completed at " + totalTime);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println();
            System.out.println(Main.HEADER);
            System.out.println(Main.BAR);

            TAM serviceTime = new TAM();
            TAM turnaroundTime = new TAM();
            TAM waitTime = new TAM();
            TAMD offeredRate = new TAMD();

            int discarded = 0;
            int size = trash.size();
            while (!trash.isEmpty()) {
                Packet p = trash.poll();
                p.print();
                if (p.wasDiscarded())
                    discarded++;
                else {
                    serviceTime.add(p.getServiceTime());
                    turnaroundTime.add(p.getTurnaroundTime());
                    waitTime.add(p.getWaitTime());
                    if (p.getInterArrivalTime() > 0)
                        offeredRate.add((p.getServiceTime() + 0.0) / p.getInterArrivalTime());
                }
            }
            System.out.println(Main.BAR);
            System.out.println(Main.HEADER);
            System.out.println();

            System.out.printf("Percent discarded: %.4f%% (%d / %d)\n",
                    ((0.0 + discarded) / size) * 100, discarded, size);

            System.out.printf("Percent processor utilization: %.4f%% (%d / %d)\n",
                    (serviceTime.getTotal().doubleValue() / totalTime) * 100,
                    serviceTime.getTotal().longValue(), totalTime);

            System.out.printf("Processor throughput: %.4f packets/s (%d packets / %.4f s)\n",
                    1000 * (size - discarded) / (totalTime + 0.0), (size - discarded), (totalTime + 0.0) / 1000);

            System.out.println();
            System.out.println("Total   service time: " + serviceTime.getTotalStr() + " ms");
            System.out.printf("Average service time: %.4f ms\n", serviceTime.getAvg());
            System.out.println("Maximum service time: " + serviceTime.getMax() + " ms");
            System.out.println();
            System.out.println("Total   wait time: " + waitTime.getTotalStr() + " ms");
            System.out.printf("Average wait time: %.4f ms\n", waitTime.getAvg());
            System.out.println("Maximum wait time: " + waitTime.getMax() + " ms");
            System.out.println();
            System.out.println("Total   turnaround time: " + turnaroundTime.getTotalStr() + " ms");
            System.out.printf("Average turnaround time: %.4f ms\n", turnaroundTime.getAvg());
            System.out.println("Maximum turnaround time: " + turnaroundTime.getMax() + " ms");
            System.out.println();
            System.out.printf("Average offered rate: %.4f\n", offeredRate.getAvg());
            System.out.println("Maximum offered rate: " + offeredRate.getMax());

            Thread.currentThread().getThreadGroup().interrupt();
        }
    }
}
