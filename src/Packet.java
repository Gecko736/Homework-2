import java.util.concurrent.Semaphore;

public class Packet implements Comparable<Packet> {
    public enum ThreadIs {
        CREATED, RUNNING, WAITING, FINISHED, DISCARDED
    }

    private static int nextPID = 0;
    private static Semaphore newID = new Semaphore(1);

    private int PID;
    private ThreadIs state;
    private final long interArrivalTime;
    private final long creationTime;
    private final long serviceTime;
    private long arrivalTime;
    private long endTime;

    private Semaphore owner = new Semaphore(1);

    public Packet(long serviceTime, long interArrivalTime) {
        this.interArrivalTime = interArrivalTime;
        try {
            newID.acquire();
            PID = nextPID++;
            newID.release();
        } catch (InterruptedException e) {}
        state = ThreadIs.CREATED;
        creationTime = Main.factory.now();
        this.serviceTime = serviceTime;
    }

    public void acquire() {
        try {
            owner.acquire();
        } catch (InterruptedException e) {}
    }

    public void release() {
        owner.release();
    }

    // ThreadIs Changers

    public void queue() {
        if (state == ThreadIs.DISCARDED)
            throw new IllegalStateException("Discarded packet " + PID + " cannot be queued.");
        if (state == ThreadIs.RUNNING)
            throw new IllegalStateException("Packet " + PID + " cannot be queued, because it is not newly created.");

        state = ThreadIs.WAITING;
        arrivalTime = Main.factory.now();
        //System.out.println("Packet " + PID + " queued at " + arrivalTime);
    }

    public void discard() {
        state = ThreadIs.DISCARDED;
        endTime = Main.factory.now();
        //System.out.println("Packet " + PID + " discarded at " + endTime);
    }

    public void start() {
        if (state == ThreadIs.DISCARDED)
            throw new IllegalStateException("Discarded packet " + PID + " cannot be started.");

        state = ThreadIs.RUNNING;
        //System.out.println("Packet " + PID + " started at " + Main.factory.now());
    }

    public void pause() {
        if (state == ThreadIs.DISCARDED)
            throw new IllegalStateException("Discarded packet " + PID + " cannot be paused.");
        if (state != ThreadIs.RUNNING)
            throw new IllegalStateException("Packet " + PID + " cannot be paused, because it is not already waiting.");

        state = ThreadIs.WAITING;
        //System.out.println("Packet " + PID + " paused at " + Main.factory.now());
    }

    public void resume() {
        if (state == ThreadIs.DISCARDED)
            throw new IllegalStateException("Discarded packet " + PID + " cannot be resumed.");
        if (state != ThreadIs.WAITING)
            throw new IllegalStateException("Packet " + PID + " cannot be resumed, because it is not already running.");

        state = ThreadIs.RUNNING;
        //System.out.println("Packet " + PID + " resumed at " + Main.factory.now());
    }

    public void finish() {
        if (state == ThreadIs.DISCARDED)
            throw new IllegalStateException("Discarded packet " + PID + " cannot be finished.");

        endTime = Main.factory.now();
        state = ThreadIs.FINISHED;
        //System.out.println("Packet " + PID + " finished at " + endTime);
    }

    public void print() {
        if (state == ThreadIs.FINISHED)
            Main.printStats(PID, interArrivalTime, creationTime, serviceTime,
                    arrivalTime, endTime, getTurnaroundTime(), getWaitTime());
        else
            Main.printStats(PID, interArrivalTime, creationTime, serviceTime, endTime);
    }

    @Override
    public int compareTo(Packet packet) {
        return PID - packet.PID;
    }

    // Getters

    public int getPID() {
        return PID;
    }

    public long getArrivalTime() {
        return arrivalTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public long getInterArrivalTime() {
        return interArrivalTime;
    }

    public long getServiceTime() {
        return serviceTime;
    }

    public long getTurnaroundTime() {
        return endTime - creationTime;
    }

    public long getWaitTime() {
        return getTurnaroundTime() - serviceTime;
    }

    public boolean wasDiscarded() {
        return state != ThreadIs.FINISHED;
    }
}
