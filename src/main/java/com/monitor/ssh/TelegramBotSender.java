package com.monitor.ssh;

import com.monitor.ssh.config.Config;
import com.monitor.ssh.info.DeviceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.DeleteMyCommands;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeChat;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

public class TelegramBotSender extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(TelegramBotSender.class);

    private final String token;
    private final String chatId;
    private final String name;

    public TelegramBotSender() {
        this.token = Config.getTELEGRAM_TOKEN();
        this.chatId = Config.getTELEGRAM_CHAT_ID();
        this.name = Config.getTELEGRAM_BOT_NAME();

        registerBotCommands();
        Runtime.getRuntime().addShutdownHook(new Thread(this::clearChatCommands));  // clears set commands on shutdown
    }

    private void clearChatCommands() {
        try {
            DeleteMyCommands deleteMyCommands = new DeleteMyCommands();
            deleteMyCommands.setScope(new BotCommandScopeChat(chatId));

            execute(deleteMyCommands);
            logger.info("Deleted Telegram commands for chat {}", chatId);
        } catch (TelegramApiException e) {
            logger.error("Failed to delete commands for chat " + chatId, e);
        }
    }


    private void registerBotCommands() {
        List<BotCommand> commandList = List.of(
                new BotCommand("/status", "Check if the bot is online"),
                new BotCommand("/info", "Get system/device information"),
                new BotCommand("/test-ssh", "Run SSH connection test"),
                new BotCommand("/shutdown", "Shutdown the monitored host")
        );

        try {
            execute(new SetMyCommands(commandList, new BotCommandScopeDefault(), null));
            logger.info("Telegram bot commands registered successfully!");
        } catch (TelegramApiException e) {
            logger.error("Failed to register Telegram commands", e);
        }
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

                    case "/test_ssh":
                        sendTelegramMessage("<b>Authorised:</b> Testing SSH connection on host...", "HTML");
                        if (!Security.testSSh()) {
                            logger.warn("SSh test failed!");
                            sendTelegramMessage("<b>SSh test failed!</b>", "HTML");
                        } else {
                            logger.info("SSH test was successful!");
                            sendTelegramMessage("<b>SSH test was successful!</b>", "HTML");
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
