package SungearEngine2D.Utils;

public class Debugger
{
    public static class CPU
    {

    }

    public static class GPU
    {

    }

    public static class Memory
    {
        public static long getCurrentHeapSizeInMB() { return Runtime.getRuntime().totalMemory() / 1024 / 1024; }

        public static long getMaxHeapSizeInMB() { return Runtime.getRuntime().maxMemory() / 1024 / 1024; }

        public static long getFreeHeapSizeInMB() { return Runtime.getRuntime().freeMemory() / 1024 / 1024; }

        public static long getHeapUsageInMB() { return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024; }
    }
}
