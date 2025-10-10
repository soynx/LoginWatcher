package com.monitor.ssh;

import com.monitor.ssh.info.AuthInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SSHMonitor {
    private static final Logger logger = LoggerFactory.getLogger(SSHMonitor.class);
    private static final TelegramBotSender telegramBotSender = new TelegramBotSender();

    private final String logFilePath;
    private final TriggerHandler triggerHandler;

    public SSHMonitor(TriggerHandler triggerHandler) throws TelegramApiException {
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

        if (telegramBotSender.getBotUsername() == null || telegramBotSender.getBotUsername().isBlank()) {
            throw new IllegalArgumentException("No bot-name provided!");
        } else {
            logger.info("Using Telegram Bot chat: {}", telegramBotSender.getBotUsername());
        }
        // ################################################################################################

        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(telegramBotSender);

        // register a task before shutting down
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("shutting down!");
            telegramBotSender.sendTelegramMessage("<b>\uD83D\uDD34 Login Monitoring disabled!</b>", "HTML");
        }));
    }

    public static void main(String[] args) throws TelegramApiException {
        ExecutorService botExecutor = Executors.newSingleThreadExecutor();
        SSHMonitor monitor = new SSHMonitor(info -> {
            botExecutor.submit(() -> {
                // implement trigger
                logger.info("Detected new login: {}", info.toString());
                StringBuilder msg = new StringBuilder("<h2>New Activity</h2>\n\n");
                msg.append("\n<b>Description:</b> ").append(info.description());
                msg.append("\n<b>Success:</b> ").append(info.success());
                msg.append("\n<b>EventType:</b> ").append(info.eventType());
                msg.append("\n<b>AuthMethod:</b> ").append(info.authMethod());
                msg.append("\n<b>User:</b> ").append(info.user());
                msg.append("\n<b>Port:</b> ").append(info.port());
                msg.append("\n<b>Ip:</b> ").append(info.ip());
                msg.append("\n<b>Log: </b><code>").append(info.rawLine()).append("</code>");
                telegramBotSender.sendTelegramMessage(msg.toString(), "HTML");
            });
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
