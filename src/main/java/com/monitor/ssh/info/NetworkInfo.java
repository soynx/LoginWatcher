package com.monitor.ssh.info;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.Enumeration;

public class NetworkInfo {

    // --- Get Private (Local) IP ---
    public static String getPrivateIp() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                if (ni.isLoopback() || !ni.isUp()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (!addr.isLoopbackAddress() && addr.isSiteLocalAddress()) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            return "Error getting private IP: " + e.getMessage();
        }
        return "Private IP not found";
    }

    // --- Get Public IP (needs Internet) ---
    public static String getPublicIp() {
        String[] services = {
            "https://api.ipify.org", 
            "https://checkip.amazonaws.com",
            "https://icanhazip.com"
        };
        for (String service : services) {
            try {
                URL url = new URL(service);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setConnectTimeout(3000);
                con.setReadTimeout(3000);

                try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                    String ip = in.readLine();
                    if (ip != null && !ip.isEmpty()) {
                        return ip.trim();
                    }
                }
            } catch (Exception ignored) {
                // Try the next service
            }
        }
        return "Public IP not found (check internet connection)";
    }
}
