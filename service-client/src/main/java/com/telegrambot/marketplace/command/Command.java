package com.telegrambot.marketplace.command;

import com.telegrambot.marketplace.dto.bot.Answer;
import com.telegrambot.marketplace.entity.user.User;
import com.telegrambot.marketplace.dto.bot.ClassifiedUpdate;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public interface Command {
    // Каким обработчиком будет пользоваться команда
    Class handler();
    // С помощью чего мы найдём эту команду
    Object getFindBy();
    // Ну и тут мы уже получим ответ на самом деле
    Answer getAnswer(ClassifiedUpdate update, User user);
}
