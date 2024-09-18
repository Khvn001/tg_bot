package com.telegrambot.marketplace.command.user.profile;

import com.telegrambot.marketplace.command.Command;
import com.telegrambot.marketplace.config.typehandlers.CallbackHandler;
import com.telegrambot.marketplace.dto.bot.Answer;
import com.telegrambot.marketplace.dto.bot.ClassifiedUpdate;
import com.telegrambot.marketplace.dto.bot.SendMessageBuilder;
import com.telegrambot.marketplace.entity.user.User;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AddBalanceCommand implements Command {

    @Value("${bot.adminUsername}")
    private String adminUsername;

    @Override
    public Class handler() {
        return CallbackHandler.class;
    }

    @Override
    public Object getFindBy() {
        return "/addBalance_";
    }

    @SneakyThrows
    @Override
    public Answer getAnswer(final ClassifiedUpdate update, final User user) {
        String message = "Для пополнения баланса свяжитесь с администратором: https://t.me/" + adminUsername
                + "?start=Здравствуйте, хочу пополнить баланс.";

        // Create a button for returning to the profile
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(InlineKeyboardButton.builder()
                .text("Return to Profile")
                .callbackData("/profile_")
                .build());

        // Return the message with the "Return to Profile" button
        return new SendMessageBuilder()
                .chatId(user.getChatId())
                .message(message)
                .buttons(buttons)  // Attach the buttons
                .build();
    }

}
