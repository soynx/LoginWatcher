package com.monitor.ssh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Objects;

public class SSHMonitor {
    private static final Logger logger = LoggerFactory.getLogger(SSHMonitor.class);
    private static final TelegramBotSender telegramBotSender = new TelegramBotSender();

    private final String logFilePath;
    private final TriggerHandler triggerHandler;

    public SSHMonitor(TriggerHandler triggerHandler) {
        this.logFilePath = System.getenv("AUTH_LOG_PATH");
        this.triggerHandler = triggerHandler;

        // validate all variables that are set over ENV
        // throws IllegalArgumentException when a variable is not properly set
        // ################################################################################################

        Security.validateConfig();

        if (Objects.isNull(logFilePath) || logFilePath.isBlank() || !new File(logFilePath).exists()) {
            throw new IllegalArgumentException("Log file path does not exist or is not set: " + logFilePath);
        } else {
            logger.info("Using logfile: {}", logFilePath);
        }

        if (telegramBotSender.getBotToken() == null || telegramBotSender.getBotToken().isBlank()) {
            throw new IllegalArgumentException("No telegram bot token provided!");
        } else {
            logger.info("Using Telegram Bot token: {}", telegramBotSender.getBotToken());
        }

        if (telegramBotSender.getChatId() == null || telegramBotSender.getChatId().isBlank()) {
            throw new IllegalArgumentException("No chat-id provided!");
        } else {
            logger.info("Using Telegram Bot chat: {}", telegramBotSender.getChatId());
        }
        // ################################################################################################


        // register a task before shutting down
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("shutting down!");
            telegramBotSender.sendTelegramMessage("<b>\uD83D\uDD34 Login Monitoring disabled!</b>", "HTML");
        }));
    }

    public static void main(String[] args) {
        SSHMonitor monitor = new SSHMonitor(info -> {
            // implement trigger
            logger.info("Detected new login: {}", info.toString());
            telegramBotSender.sendTelegramMessage("<b>New login:</b>    " + info, "HTML");
        });
        monitor.startMonitoring();
    }

    public void startMonitoring() {
        logger.info("Monitoring started!");
        telegramBotSender.sendTelegramMessage("<b>\uD83D\uDFE2 Login Monitoring started!</b>", "HTML");

        try (RandomAccessFile file = new RandomAccessFile(logFilePath, "r")) {
            file.seek(file.length()); // jump to end of the file

            while (true) {
                String line = file.readLine();
                if (line != null) {
                    AuthInfo info = SSHLogParser.parseLine(line);
                    if (info != null) {
                        triggerHandler.trigger(info);
                    }
                } else {
                    Thread.sleep(500);
                }
            }
        } catch (IOException | InterruptedException e) {
            logger.error("Error while monitoring log file", e);
        }
    }
}
