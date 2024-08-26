package com.telegrambot.marketplace;

import com.telegrambot.marketplace.config.typehandlers.ClassifiedUpdateHandler;
import com.telegrambot.marketplace.dto.bot.Answer;
import com.telegrambot.marketplace.dto.bot.ClassifiedUpdate;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Comparator;
import java.util.List;

@Component
@Slf4j
public class Bot extends TelegramLongPollingBot {

    @Value("${bot.name}")
    private String botName;

    private final ClassifiedUpdateHandler updateHandler;

    public Bot(final @Value("${bot.token}")String botToken, final ClassifiedUpdateHandler updateHandler) {
        super(botToken);
        this.updateHandler = updateHandler;
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void onUpdateReceived(final @NotNull Update update) {
        log.info("Update received:");
        //Проверим, работает ли наш бот.

        if (update.hasMessage()) {
            if (update.getMessage().hasText()) {
                String messageText = update.getMessage().getText();
                log.info("Message text: " + messageText);

                // Convert Telegram Update to ClassifiedUpdate
                ClassifiedUpdate classifiedUpdate = new ClassifiedUpdate(update);
                log.info(classifiedUpdate.toString());

                // Process the update
                Answer answer = updateHandler.request(classifiedUpdate);

                // Send a response (assuming Answer contains a method to get the response text)
                sendMessage(answer);
            } else if (update.getMessage().hasPhoto()) {
                List<PhotoSize> photos = update.getMessage().getPhoto();
                PhotoSize largestPhoto = photos.stream().max(Comparator.comparing(PhotoSize::getFileSize))
                        .orElse(null);
                if (largestPhoto != null) {
                    String fileId = largestPhoto.getFileId();
                    log.info("Message photo: " + fileId);
                }
                ClassifiedUpdate classifiedUpdate = new ClassifiedUpdate(update);
                log.info(classifiedUpdate.toString());

                // Process the update
                Answer answer = updateHandler.request(classifiedUpdate);

                // Send a response (assuming Answer contains a method to get the response text)
                sendMessage(answer);
            }
        }

        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String data = callbackQuery.getData();
            log.info(data);

            // Convert Telegram Update to ClassifiedUpdate
            ClassifiedUpdate classifiedUpdate = new ClassifiedUpdate(update);
            log.info(classifiedUpdate.toString());

            // Process the update
            Answer answer = updateHandler.request(classifiedUpdate);
            log.info(answer.toString());

            // Send a response (assuming Answer contains a method to get the response text)
            sendMessage(answer);
        }
    }

    private void sendMessage(final Answer answer) {
        try {
            if (answer.getBotApiMethod() instanceof SendMessage message) {
                execute(message);
            } else {
                log.error("Unsupported message type: {}", answer.getBotApiMethod().getClass().getName());
            }
        } catch (TelegramApiException e) {
            log.error("Error sending message: ", e);
        }
    }


}
