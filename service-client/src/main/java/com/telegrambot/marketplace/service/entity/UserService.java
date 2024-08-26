package com.telegrambot.marketplace.service.entity;

import com.telegrambot.marketplace.dto.bot.ClassifiedUpdate;
import com.telegrambot.marketplace.entity.user.User;

import java.math.BigDecimal;

public interface UserService {

    User findUserByUpdate(ClassifiedUpdate classifiedUpdate);

    User save(User user);

    User findByChatId(String chatId);

    void addUserBalance(User user, BigDecimal amount);



}
