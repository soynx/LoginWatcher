package com.monitor.ssh;

import com.monitor.ssh.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class NtfySender {

    private static final Logger logger = LoggerFactory.getLogger(NtfySender.class);

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final String url;
    private final String token;

    public NtfySender() {
        this.url = Config.getNTFY_URL();
        this.token = Config.getNTFY_TOKEN();
    }

    public void sendNtfyMessage(String message) {
        try {
            // ntfy renders plain text, so strip the Telegram HTML tags
            String plainMessage = message.replaceAll("<[^>]+>", "");

            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .POST(HttpRequest.BodyPublishers.ofString(plainMessage, StandardCharsets.UTF_8));

            if (token != null && !token.isBlank()) {
                requestBuilder.header("Authorization", "Bearer " + token);
            }

            HttpResponse<String> response = client.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() / 100 != 2) {
                logger.error("Failed to send NTFY message: HTTP {} - {}", response.statusCode(), response.body());
            } else {
                logger.debug("Sent NTFY message successfully!");
            }
        } catch (Exception e) {
            logger.error("Failed to send NTFY message: ", e);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
