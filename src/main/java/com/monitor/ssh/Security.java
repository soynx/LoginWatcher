package com.monitor.ssh;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

public class Security {

    private static final Logger logger = LoggerFactory.getLogger(Security.class);

    public static void validateConfig() throws IllegalArgumentException {
        String host = System.getenv("SSH_HOST");
        String user = System.getenv("SSH_USER");
        String password = System.getenv("SSH_PASSWORD");

        String portStr = System.getenv("SSH_PORT");
        int port = (portStr != null && !portStr.isEmpty()) ? Integer.parseInt(portStr) : 22;

        if (host == null || host.isBlank() || user == null  || user.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("SSH configuration missing. Please set SSH_HOST, SSH_USER, SSH_PASSWORD, SSH_PORT (optional).");
        } else {
            logger.info("Using SSH host: {}", host);
            logger.info("Using SSH user: {}", user);
            logger.info("Using SSH password: {}", password);
            logger.info("Using SSH port: {}", port);
        }
    }

    public static boolean shutdownHost() {
        String host = System.getenv("SSH_HOST");
        String portStr = System.getenv("SSH_PORT");
        String user = System.getenv("SSH_USER");
        String password = System.getenv("SSH_PASSWORD");

        int port = (portStr != null && !portStr.isEmpty()) ? Integer.parseInt(portStr) : 22;

        Session session = null;
        ChannelExec channel = null;

        try {
            JSch jsch = new JSch();
            session = jsch.getSession(user, host, port);
            session.setPassword(password);

            // Disable strict host key checking for simplicity (not recommended for production)
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            logger.info("Connecting to {}:{} via SSH...", host, port);
            session.connect(10000); // 10-second timeout

            String command = "shutdown -h now";
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            channel.setErrStream(System.err);

            InputStream in = channel.getInputStream();
            channel.connect();

            logger.info("Executing shutdown command on host...");
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    logger.info(new String(tmp, 0, i));
                }
                if (channel.isClosed()) {
                    logger.info("Exit status: {}", channel.getExitStatus());
                    break;
                }
                Thread.sleep(1000);
            }

        } catch (Exception e) {
            logger.error("Error during SSH shutdown: ", e);
            return false;
        } finally {
            if (channel != null) channel.disconnect();
            if (session != null) session.disconnect();
            return true;
        }
    }
}
