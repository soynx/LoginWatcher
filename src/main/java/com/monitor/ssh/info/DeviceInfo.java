package com.monitor.ssh.info;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;

public class DeviceInfo {

    // --- JVM & Runtime Info ---
    public static long getJvmUptimeMillis() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        return runtimeMXBean.getUptime();
    }

    public static String getJavaVersion() {
        return System.getProperty("java.version");
    }

    public static String getJavaVendor() {
        return System.getProperty("java.vendor");
    }

    public static String getJavaHome() {
        return System.getProperty("java.home");
    }

    // --- OS Info ---
    public static String getOsName() {
        return System.getProperty("os.name");
    }

    public static String getOsVersion() {
        return System.getProperty("os.version");
    }

    public static String getOsArchitecture() {
        return System.getProperty("os.arch");
    }

    // --- User Info ---
    public static String getUserName() {
        return System.getProperty("user.name");
    }

    public static String getUserHome() {
        return System.getProperty("user.home");
    }

    public static String getUserDir() {
        return System.getProperty("user.dir");
    }

    // --- CPU & Memory ---
    public static int getAvailableProcessors() {
        return Runtime.getRuntime().availableProcessors();
    }

    public static long getMaxMemory() {
        return Runtime.getRuntime().maxMemory();
    }

    public static long getTotalMemory() {
        return Runtime.getRuntime().totalMemory();
    }

    public static long getFreeMemory() {
        return Runtime.getRuntime().freeMemory();
    }

    // --- System Load (may not work everywhere) ---
    public static double getSystemLoadAverage() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        return osBean.getSystemLoadAverage();
    }

    public static String getMessageFormat() {
        StringBuilder sb = new StringBuilder();
        sb.append("<b>Information:</b>\n")
                .append("<b>Public IP:</b> ").append(NetworkInfo.getPublicIp()).append("\n")
                .append("<b>Private IP:</b> ").append(NetworkInfo.getPrivateIp());

        sb.append("\n\n<b>CPU and Memory:</b>\n")
                .append("<b>Max Mem:</b> ").append(getMaxMemory()).append("\n")
                .append("<b>Total Mem:</b> ").append(getTotalMemory()).append("\n")
                .append("<b>Free Mem:</b> ").append(getFreeMemory());

        sb.append("\n\n<b>Device:</b>\n")
                .append("<b>OS Name:</b> ").append(getOsName()).append("\n")
                .append("<b>OS Version:</b> ").append(getOsVersion()).append("\n")
                .append("<b>Java Version:</b> ").append(getJavaVersion());

        return sb.toString();
    }
}
