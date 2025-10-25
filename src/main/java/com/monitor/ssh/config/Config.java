package com.monitor.ssh.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public class Config {

    public final static Logger logger = LoggerFactory.getLogger(Config.class);

    private static final String SSH_HOST = System.getenv("SSH_HOST");
    private static final String SSH_PRIVATE_KEY = System.getenv("SSH_PRIVATE_KEY");
    private static final String SSH_PRIVATE_KEY_PASSPHRASE = System.getenv("SSH_PRIVATE_KEY_PASSPHRASE");
    private static final String SSH_USERNAME = System.getenv("SSH_USERNAME");
    private static final String SSH_PASSWORD = System.getenv("SSH_PASSWORD");
    private static final String SSH_PORT = System.getenv("SSH_PORT");

    private static final String TELEGRAM_TOKEN = System.getenv("TELEGRAM_TOKEN");
    private static final String TELEGRAM_CHAT_ID = System.getenv("TELEGRAM_CHAT_ID");
    private static final String TELEGRAM_BOT_NAME = System.getenv("TELEGRAM_BOT_NAME");

    private static final String NOTIFY_SHOW_LOG = System.getenv("NOTIFY_SHOW_LOG");
    private static final String NOTIFY_SUCCESS = System.getenv("NOTIFY_SUCCESS");
    private static final String NOTIFY_FAIL = System.getenv("NOTIFY_FAIL");
    private static final String NOTIFY_DISCONNECT = System.getenv("NOTIFY_DISCONNECT");
    private static final String NOTIFY_INVALID_USER = System.getenv("NOTIFY_INVALID_USER");
    private static final String NOTIFY_CLOSE_SESSION = System.getenv("NOTIFY_CLOSE_SESSION");
    private static final String NOTIFY_IGNORE_CONTENTS = System.getenv("NOTIFY_IGNORE_CONTENTS");
    private static final String NOTIFY_WHITELIST = System.getenv("NOTIFY_WHITELIST");

    private static final String AUTH_LOG_PATH = System.getenv("AUTH_LOG_PATH");

    private static void exitOnConfig(String name) {
        logger.error("Env '{}' is required!", name);
        System.exit(1);
    }

    private static void exitOnNull(String var, String name) {
        if (var == null || var.isBlank()) {
            exitOnConfig(name);
        }
    }

    public static void exitOnFalseConfig() {
        // call all getters that will exit once there is a false config
        getSSH_HOST();
        getSSH_USERNAME();
        getTELEGRAM_BOT_NAME();
        getTELEGRAM_CHAT_ID();
        getTELEGRAM_TOKEN();
        getAuthLogPath();
    }

    public static boolean getNOTIFY_SHOW_LOG() {
        return NOTIFY_SHOW_LOG != null && NOTIFY_SHOW_LOG.equals("true");
    }

    public static boolean getNOTIFY_SUCCESS() {
        return NOTIFY_SUCCESS == null || NOTIFY_SUCCESS.equals("true");
    }

    public static boolean getNOTIFY_DISCONNECT() {
        return NOTIFY_DISCONNECT == null || NOTIFY_DISCONNECT.equals("true");
    }

    public static boolean getNOTIFY_FAIL() {
        return NOTIFY_FAIL == null || NOTIFY_FAIL.equals("true");
    }

    public static boolean getNOTIFY_CLOSE_SESSION() {
        return NOTIFY_CLOSE_SESSION == null || NOTIFY_CLOSE_SESSION.equals("true");
    }

    public static boolean getNOTIFY_INVALID_USER() {
        return NOTIFY_INVALID_USER == null || NOTIFY_INVALID_USER.equals("true");
    }

    public static String getNOTIFY_IGNORE_CONTENTS() {
        return NOTIFY_IGNORE_CONTENTS;
    }

    public static String getSSH_HOST() {
        exitOnNull(SSH_HOST, "SSH_HOST");
        return SSH_HOST;
    }

    public static String getSSH_PRIVATE_KEY() {
        return SSH_PRIVATE_KEY;
    }
    public static String getSSH_PRIVATE_KEY_PASSPHRASE() {
        return SSH_PRIVATE_KEY_PASSPHRASE;
    }

    public static String getSSH_USERNAME() {
        exitOnNull(SSH_USERNAME, "SSH_USERNAME");
        return SSH_USERNAME;
    }

    public static String getSSH_PASSWORD() {
        return SSH_PASSWORD;
    }

    public static int getSSH_PORT() {
        if (SSH_PORT == null || SSH_PORT.isBlank()) {
            return 22;
        }

        try {
            return Integer.parseInt(SSH_PORT);
        } catch (NumberFormatException e) {
            return 22;
        }
    }

    public static String getTELEGRAM_TOKEN() {
        exitOnNull(TELEGRAM_TOKEN, "TELEGRAM_TOKEN");
        return TELEGRAM_TOKEN;
    }

    public static String getTELEGRAM_CHAT_ID() {
        exitOnNull(TELEGRAM_CHAT_ID, "TELEGRAM_CHAT_ID");
        return TELEGRAM_CHAT_ID;
    }

    public static String getTELEGRAM_BOT_NAME() {
        exitOnNull(TELEGRAM_BOT_NAME, "TELEGRAM_BOT_NAME");
        return TELEGRAM_BOT_NAME;
    }

    public static String[] getNOTIFY_WHITELIST() {
        return NOTIFY_WHITELIST.split(";");
    }

    public static String getAuthLogPath() {
        exitOnNull(AUTH_LOG_PATH, "AUTH_LOG_PATH");
        File log = new File(AUTH_LOG_PATH);
        if (log.exists() && !log.isDirectory()) {
            return log.getAbsolutePath();
        }
        logger.error("The given log file can not be found!");
        System.exit(1);
        return null;
    }
}
