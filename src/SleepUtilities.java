public class SleepUtilities {
    public static void rest(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {}
    }
}
