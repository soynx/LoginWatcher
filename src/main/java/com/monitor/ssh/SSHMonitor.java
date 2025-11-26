package com.monitor.ssh;

import com.monitor.ssh.config.Config;
import com.monitor.ssh.info.AuthInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

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
    private final LogBuffer logBuffer = new LogBuffer(100);

    public SSHMonitor(TriggerHandler triggerHandler) throws TelegramApiException {
        Config.exitOnFalseConfig();
        this.logFilePath = Config.getAuthLogPath();
        this.triggerHandler = triggerHandler;

        // ################################################################################################

        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(telegramBotSender);

        if (Config.getNOTIFY_SHUTDOWN()) {
            // register a task before shutting down
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("shutting down!");
                telegramBotSender.sendTelegramMessage("<b>\uD83D\uDD34 Login Monitoring disabled!</b>", "HTML");
            }));
        }
    }

    public static void main(String[] args) throws TelegramApiException {
        ExecutorService botExecutor = Executors.newSingleThreadExecutor();
        SSHMonitor monitor = new SSHMonitor(info -> {
            botExecutor.submit(() -> {
                // implement trigger
                logger.info("Detected new login: {}", info.toString());
                StringBuilder msg = new StringBuilder("<b>New Activity</b>\n\n");
                msg.append("\n<b>Description:</b> ").append(info.description());
                msg.append("\n<b>Success:</b> ").append(info.success());
                msg.append("\n<b>EventType:</b> ").append(info.eventType());
                msg.append("\n<b>AuthMethod:</b> ").append(info.authMethod());
                msg.append("\n<b>User:</b> ").append(info.user());
                msg.append("\n<b>Port:</b> ").append(info.port());
                msg.append("\n<b>Ip:</b> ").append(info.ip());
                if (Config.getNOTIFY_SHOW_LOG()) {
                    msg.append("\n<b>Log: </b><code>").append(info.rawLine()).append("</code>");
                }
                telegramBotSender.sendTelegramMessage(msg.toString(), "HTML");
            });
        });
        monitor.startMonitoring();
    }

    public void startMonitoring() {
        logger.info("Monitoring started!");

        if (Config.getNOTIFY_STARTUP()) {
            telegramBotSender.sendTelegramMessage("<b>\uD83D\uDFE2 Login Monitoring started!</b>", "HTML");
        }

        try (RandomAccessFile file = new RandomAccessFile(logFilePath, "r")) {
            file.seek(file.length()); // jump to end of the file

            while (true) {
                String line = file.readLine();
                if (line != null) {
                    if (!logBuffer.contains(line)) {
                        logBuffer.addLog(line);
                        AuthInfo info = SSHLogParser.parseLine(line);
                        if (info != null) {
                            if (isWhitelisted(info)) {
                                logger.info("Skipping Notification for IP '{}' due to whitelist! Auth Information: {}", info.ip(), info);
                            } else {
                                triggerHandler.trigger(info);
                            }
                        }
                    }
                } else {
                    Thread.sleep(1000);
                }
            }
        } catch (IOException | InterruptedException e) {
            logger.error("Error while monitoring log file", e);
        }
    }

    private boolean isWhitelisted(AuthInfo info) {
        for (String ip : Config.getNOTIFY_WHITELIST()) {
            if (Objects.equals(info.ip(), ip.strip())) {
                return true;
            }
        }
        return false;
    }
}
