import java.math.BigDecimal;
import java.math.BigInteger;

public class TAM {
    private BigInteger total = BigInteger.ZERO;
    private long size = 0;
    private long max = 0;

    public void add(long num) {
        total = total.add(BigInteger.valueOf(num));
        size++;
        if (num > max)
            max = num;
    }

    public String getTotalStr() {
        return total.toString();
    }

    public BigDecimal getTotal() {
        return BigDecimal.valueOf(total.longValue());
    }

    public double getAvg() {
        return total.doubleValue() / size;
    }

    public long getMax() {
        return max;
    }
}
