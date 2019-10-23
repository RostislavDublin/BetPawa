package rdublin.utils;

public final class MetricsUtils {

    public static final long getDuration(long start) {
        return System.currentTimeMillis() - start;
    }
}
