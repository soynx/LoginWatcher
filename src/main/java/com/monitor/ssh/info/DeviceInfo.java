package com.monitor.ssh.info;

import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class DeviceInfo {

    // --- JVM & Runtime Info ---
    private static long getJvmUptimeMillis() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        return runtimeMXBean.getUptime();
    }

    private static String getJavaVersion() {
        return System.getProperty("java.version");
    }

    private static String getJavaVendor() {
        return System.getProperty("java.vendor");
    }

    private static String getJavaHome() {
        return System.getProperty("java.home");
    }

    // --- OS Info ---
    private static String getOsName() {
        return System.getProperty("os.name");
    }

    private static String getOsVersion() {
        return System.getProperty("os.version");
    }

    private static String getOsArchitecture() {
        return System.getProperty("os.arch");
    }

    // --- User Info ---
    private static String getUserName() {
        return System.getProperty("user.name");
    }

    private static String getUserHome() {
        return System.getProperty("user.home");
    }

    private static String getUserDir() {
        return System.getProperty("user.dir");
    }

    // --- CPU & Memory ---
    private static int getAvailableProcessors() {
        return Runtime.getRuntime().availableProcessors();
    }

    private static String getMaxMemory() {
        return formatBytes(Runtime.getRuntime().maxMemory());
    }

    private static String getTotalMemory() {
        return formatBytes(Runtime.getRuntime().totalMemory());
    }

    private static String getFreeMemory() {
        return formatBytes(Runtime.getRuntime().freeMemory());
    }

    private static String formatBytes(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String unit = "KMGTPE".charAt(exp - 1) + "B"; // KB, MB, GB, ...
        double value = bytes / Math.pow(1024, exp);
        return String.format("%.2f %s", value, unit);
    }

    public static String getJvmUptime() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        long uptimeMillis = runtimeMXBean.getUptime();

        long days = TimeUnit.MILLISECONDS.toDays(uptimeMillis);
        long hours = TimeUnit.MILLISECONDS.toHours(uptimeMillis) % 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(uptimeMillis) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(uptimeMillis) % 60;

        return String.format("%02dd %02dh %02dm %02ds", days, hours, minutes, seconds);
    }

    public static String getMessageFormat() {
        StringBuilder sb = new StringBuilder();
        sb.append("<b>App Version: </b>").append(ProjectInfo.getProjectVersion());
        sb.append("\n\n<b>JVM Uptime:</b>\n").append(getJvmUptime());

        sb.append("\n\n<b>Networking:</b>\n")
                .append("<b>Public IP:</b> ").append(NetworkInfo.getPublicIp()).append("\n")
                .append("<b>Private IP:</b> ").append(NetworkInfo.getPrivateIp());

        sb.append("\n\n<b>CPU and Memory:</b>\n")
                .append("<b>Max Mem:</b> ").append(getMaxMemory()).append("\n")
                .append("<b>Total Mem:</b> ").append(getTotalMemory()).append("\n")
                .append("<b>Free Mem:</b> ").append(getFreeMemory()).append("\n")
                .append("<b>Processors: </b>").append(getAvailableProcessors());

        sb.append("\n\n<b>Device:</b>\n")
                .append("<b>Java Version:</b> ").append(getJavaVersion()).append("\n")
                .append("<b>Java Vendor:</b> ").append(getJavaVendor()).append("\n")
                .append("<b>Java Home:</b> ").append(getJavaHome()).append("\n")
                .append("<b>OS Name:</b> ").append(getOsName()).append("\n")
                .append("<b>OS Version:</b> ").append(getOsVersion()).append("\n")
                .append("<b>OS Architecture:</b> ").append(getOsArchitecture()).append("\n")
                .append("<b>User Name:</b> ").append(getUserName()).append("\n")
                .append("<b>User Home:</b> ").append(getUserHome()).append("\n")
                .append("<b>User Dir:</b> ").append(getUserDir());

        return sb.toString();
    }

    private class ProjectInfo {

        public static String getProjectVersion() {
            try (InputStream input = ProjectInfo.class.getClassLoader().getResourceAsStream("version.properties")) {
                if (input == null) {
                    return "Version not found";
                }
                Properties props = new Properties();
                props.load(input);
                return props.getProperty("version", "Unknown");
            } catch (IOException e) {
                return "Error reading version: " + e.getMessage();
            }
        }
    }
}
