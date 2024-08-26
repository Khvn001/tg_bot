package com.telegrambot.marketplace.config.typehandlers;

import com.telegrambot.marketplace.dto.bot.Answer;
import com.telegrambot.marketplace.dto.bot.ClassifiedUpdate;
import com.telegrambot.marketplace.service.entity.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class ClassifiedUpdateHandler {

    private final UserService userService;

    private final HandlersMap commandMap;

    public Answer request(final ClassifiedUpdate classifiedUpdate) {
        log.info(classifiedUpdate.getLog());
        return commandMap.execute(classifiedUpdate,
                userService.findUserByUpdate(classifiedUpdate));
    }
}
