public class Producer extends Thread {
    private final long interArrivalTime;
    private final long serviceTime;

    public Producer(long interArrivalTime, long serviceTime) {
        this.interArrivalTime = interArrivalTime;
        this.serviceTime = serviceTime;
    }

    @Override
    public void run() {
        try {
            while (Main.factory.check() && Main.factory.running) {
                long IAtime;
                if (Main.factory.randomize)
                    IAtime = (long) ((interArrivalTime + 0.5) * -Math.log(Math.random()));
                else
                    IAtime = interArrivalTime;

                try {
                    sleep(IAtime);
                } catch (InterruptedException e) {
                }

                Packet packet;
                if (Main.factory.randomize)
                    packet = new Packet((long) ((serviceTime + 0.5) * -Math.log(Math.random())), IAtime);
                else
                    packet = new Packet(serviceTime, interArrivalTime);

                packet.acquire();
                try {
                    Main.factory.queue(packet);
                    packet.queue();
                } catch (IllegalStateException e) {
                    packet.discard();
                    Main.factory.throwAway(packet);
                }
                packet.release();
            }
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " interrupted");
        }
    }
}
