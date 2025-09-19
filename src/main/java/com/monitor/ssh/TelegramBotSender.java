package com.monitor.ssh;

import com.monitor.ssh.info.DeviceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramBotSender extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(TelegramBotSender.class);

    private final String token;
    private final String chatId;
    private final String name;

    public TelegramBotSender() {
        this.token = System.getenv("TELEGRAM_TOKEN");
        this.chatId = System.getenv("TELEGRAM_CHAT_ID");
        this.name = System.getenv("TELEGRAM_BOT_NAME");
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText().trim();
            Long incomingChatId = update.getMessage().getChatId();

            logger.info("Received message: {}", text);

            // Validate chat ID
            if (incomingChatId.toString().equals(chatId)) {
                switch (text.toLowerCase()) {
                    case "/shutdown":
                        sendTelegramMessage("<b>Authorised:</b> Executing shutdown...", "HTML");
                        if (!Security.shutdownHost()) {
                            logger.warn("Failed to shutdown host");
                            sendTelegramMessage("<b>Failed to shutdown host</b>", "HTML");
                        }
                        break;

                    case "/status":
                        sendTelegramMessage("<b>Bot is online</b>", "HTML");
                        break;

                    case "/info":
                        String message = DeviceInfo.getMessageFormat();
                        sendTelegramMessage(message, "HTML");
                        break;

                    default:
                        sendTelegramMessage("<b>Unknown command:</b> " + text, "HTML");
                        break;
                }
            } else {
                logger.warn("Unauthorised chat attempted to send command: {}", incomingChatId);
                sendTelegramMessage("<b>Access denied:</b> Unauthorised chat.", "HTML");
            }
        }
    }


    public void sendTelegramMessage(String message, String parseMode) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        if (parseMode != null && !parseMode.isEmpty()) {
            sendMessage.setParseMode(parseMode);
        }

        try {
            execute(sendMessage);
            logger.debug("Sent Telegram message successfully!");
        } catch (TelegramApiException e) {
            logger.error("Failed to send Telegram message: ", e);
        }
    }

    @Override
    public String getBotUsername() {
        return this.name;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    public String getChatId() {
        return chatId;
    }
}
