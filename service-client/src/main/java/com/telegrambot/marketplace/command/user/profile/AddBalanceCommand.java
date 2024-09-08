package com.telegrambot.marketplace.command.user.profile;

import com.telegrambot.marketplace.command.Command;
import com.telegrambot.marketplace.config.typehandlers.CallbackHandler;
import com.telegrambot.marketplace.dto.bot.Answer;
import com.telegrambot.marketplace.dto.bot.ClassifiedUpdate;
import com.telegrambot.marketplace.dto.bot.SendMessageBuilder;
import com.telegrambot.marketplace.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AddBalanceCommand implements Command {

    @Value("${bot.adminUsername}")
    private String adminUsername;

    @Override
    public Class handler() {
        return CallbackHandler.class;
    }

    @Override
    public Object getFindBy() {
        return "/add_balance_";
    }

    @SneakyThrows
    @Override
    public Answer getAnswer(final ClassifiedUpdate update, final User user) {
        String message = "Для пополнения баланса свяжитесь с администратором: https://t.me/" + adminUsername
                + "?start=Здравствуйте, хочу пополнить баланс.";

        return new SendMessageBuilder()
                .chatId(user.getChatId())
                .message(message)
                .build();
    }
}
