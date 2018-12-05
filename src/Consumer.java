import java.util.LinkedList;

public class Consumer extends Thread {
    @Override
    public void run() {
        while (Main.factory.check()) {
            Packet packet = Main.factory.poll();
            if (packet != null) {
                packet.acquire();
                packet.start();
                if (Main.factory.randomize)
                    SleepUtilities.rest(packet.getServiceTime());
                else
                    SleepUtilities.rest((long) ((packet.getServiceTime() + 0.5) * -Math.log(Math.random())));

                packet.finish();
                Main.factory.throwAway(packet);
                packet.release();
            }
        }
    }
}
