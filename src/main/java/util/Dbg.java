package util;

public final class Dbg {
    public static boolean ON = true;

    public static void log(String tag, String fmt, Object... args) {
        if (!ON) return;
        System.out.printf("[%s] %s%n", tag, String.format(fmt, args));
    }
}
