package com.monitor.ssh;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class Security {

    private static final Logger logger = LoggerFactory.getLogger(Security.class);

    public static void validateConfig() {
        String host = System.getenv("SSH_HOST");
        String portStr = System.getenv("SSH_PORT");
        String user = System.getenv("SSH_USER");
        String password = System.getenv("SSH_PASSWORD");
        String privateKey = System.getenv("SSH_PRIVATE_KEY");

        int port = (portStr != null && !portStr.isEmpty()) ? Integer.parseInt(portStr) : 22;

        if (host == null || host.isBlank() || user == null || user.isBlank()) {
            throw new IllegalArgumentException("SSH configuration missing. Please set SSH_HOST, SSH_USER, SSH_PASSWORD, SSH_PORT (optional).");

        } else if ((password == null || password.isBlank()) && (privateKey == null || privateKey.isBlank())) {
            throw new IllegalArgumentException("SSH configuration missing. Please set at least a SSH_PASSWORD or SSH_PRIVATE_KEY.");
        } else {
            logger.info("Using SSH host: {}", host);
            logger.info("Using SSH user: {}", user);
            logger.info("Using SSH port: {}", port);
        }
    }

    public static boolean shutdownHost() {
        String host = System.getenv("SSH_HOST");
        String portStr = System.getenv("SSH_PORT");
        String user = System.getenv("SSH_USER");
        String password = System.getenv("SSH_PASSWORD");
        String privateKey = System.getenv("SSH_PRIVATE_KEY");
        String privateKeyPassphrase = System.getenv("SSH_PRIVATE_KEY_PASSPHRASE"); // optional

        int port = (portStr != null && !portStr.isEmpty()) ? Integer.parseInt(portStr) : 22;

        Session session = null;
        ChannelExec channel = null;
        boolean success = false;

        try {
            JSch jsch = new JSch();

            // --- Add private key if provided ---
            if (privateKey != null && !privateKey.isEmpty()) {
                if (privateKeyPassphrase != null && !privateKeyPassphrase.isEmpty()) {
                    jsch.addIdentity(privateKey, privateKeyPassphrase);
                } else {
                    jsch.addIdentity(privateKey);
                }
            }

            session = jsch.getSession(user, host, port);

            // --- Use password only if no key was provided ---
            if (password != null && !password.isEmpty() && (privateKey == null || privateKey.isEmpty())) {
                session.setPassword(password);
            }

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            logger.info("Connecting to {}:{} via SSH...", host, port);
            session.connect(10000);

            String command = "sudo -n shutdown -h now || shutdown -h now";
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            channel.setErrStream(System.err);

            InputStream in = channel.getInputStream();
            channel.connect();

            logger.info("Executing shutdown command on host...");
            byte[] tmp = new byte[1024];
            long startTime = System.currentTimeMillis();
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    logger.info(new String(tmp, 0, i, StandardCharsets.UTF_8));
                }
                if (channel.isClosed()) {
                    logger.info("Exit status: {}", channel.getExitStatus());
                    success = (channel.getExitStatus() == 0);
                    break;
                }
                if (System.currentTimeMillis() - startTime > 15000) {
                    logger.warn("Timeout waiting for shutdown command to complete.");
                    break;
                }
                Thread.sleep(500);
            }

        } catch (Exception e) {
            logger.error("Error during SSH shutdown: ", e);
        } finally {
            if (channel != null) channel.disconnect();
            if (session != null) session.disconnect();
        }
        return success;
    }
}
