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
        StringBuffer sb = new StringBuffer();
        sb.append("<b>Information:</b><br>")
                .append("<b>Public IP:</b> ").append(NetworkInfo.getPublicIp()).append("<br>")
                .append("<b>Private IP:</b> ").append(NetworkInfo.getPrivateIp());

        sb.append("<br><br><b>CPU and Memory:</b><br>")
                .append("<b>Max Mem:</b> ").append(getMaxMemory()).append("<br>")
                .append("<b>Total Mem:</b> ").append(getTotalMemory()).append("<br>")
                .append("<b>Free Mem:</b> ").append(getFreeMemory());

        sb.append("<br><br><b>Device:</b><br>")
                .append("<b>OS Name:</b> ").append(getOsName()).append("<br>")
                .append("<b>OS Version:</b> ").append(getOsVersion()).append("<br>")
                .append("<b>Java Version:</b> ").append(getJavaVersion());

        return sb.toString();
    }
}
