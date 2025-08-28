package com.monitor.ssh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;


public class TelegramBotSender {

    public final String TOKEN;
    public final String CHAT;
    private final Logger logger = LoggerFactory.getLogger(TelegramBotSender.class);

    public TelegramBotSender() {
        this.TOKEN = System.getenv("TELEGRAM_TOKEN");
        this.CHAT = System.getenv("TELEGRAM_CHAT_ID");
    }

    public void sendTelegramMessage(String message, String parseMode) {
        try {
            String urlString = "https://api.telegram.org/bot" + TOKEN + "/sendMessage";
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            String payload = "chat_id=" + CHAT +
                             "&text=" + java.net.URLEncoder.encode(message, StandardCharsets.UTF_8) +
                             "&parse_mode=" + parseMode;

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(payload.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                logger.debug("Sent Bot message successfully!");
            } else {
                logger.warn("Could not send message HTTP response code: {}", responseCode);
            }

        } catch (Exception e) {
            logger.error("Issued Exception while sending a message: {}", e.getMessage());
        }
    }
}
