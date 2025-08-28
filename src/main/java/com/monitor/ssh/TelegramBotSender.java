package com.monitor.ssh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            // Escape quotes for JSON
            String jsonMessage = message.replace("\"", "\\\"");

            String jsonPayload = String.format(
                    "{\"chat_id\":\"%s\",\"text\":\"%s\",\"parse_mode\":\"%s\"}",
                    CHAT,
                    jsonMessage,
                    parseMode
            );

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                logger.debug("Sent Telegram message successfully!");
            } else {
                // Read error response from Telegram
                StringBuilder errorResponse = new StringBuilder();
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        errorResponse.append(line);
                    }
                }
                logger.warn("Failed to send Telegram message. HTTP Code: {}. Response: {}", responseCode, errorResponse);
            }

        } catch (Exception e) {
            logger.error("Exception while sending Telegram message: ", e);
        }
    }
}
