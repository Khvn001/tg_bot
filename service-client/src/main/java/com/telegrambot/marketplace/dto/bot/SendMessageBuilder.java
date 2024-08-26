package com.telegrambot.marketplace.dto.bot;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class SendMessageBuilder {
    private SendMessage sendMessage = new SendMessage();

    public SendMessageBuilder chatId(final Long chatId) {
        this.sendMessage.setChatId(chatId);
        return this;
    }

    public SendMessageBuilder message(final String message) {
        this.sendMessage.setText(message);
        return this;
    }

    public SendMessageBuilder buttons(final List<InlineKeyboardButton> buttons) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (InlineKeyboardButton button : buttons) {
            rows.add(Collections.singletonList(button));
        }
        markup.setKeyboard(rows);
        this.sendMessage.setReplyMarkup(markup);
        return this;
    }


    public Answer build() {
        Answer answer = new Answer();
        answer.setBotApiMethod(sendMessage);
        return answer;
    }
}
