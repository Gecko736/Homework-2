import java.math.BigDecimal;

public class TAMD {
    private BigDecimal total = BigDecimal.ZERO;
    private long size = 0;
    private double max;

    public void add(double num) {
        total = total.add(BigDecimal.valueOf(num));
        size++;
        if (num > max)
            max = num;
    }

    public double getAvg() {
        return total.doubleValue() / size;
    }

    public double getMax() {
        return max;
    }
}
