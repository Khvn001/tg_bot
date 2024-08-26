package com.telegrambot.marketplace.config.typehandlers;

import com.telegrambot.marketplace.command.Command;
import com.telegrambot.marketplace.dto.bot.Answer;
import com.telegrambot.marketplace.dto.bot.ClassifiedUpdate;
import com.telegrambot.marketplace.entity.user.User;
import com.telegrambot.marketplace.enums.TelegramType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@AllArgsConstructor
@Slf4j
public class PhotoHandler extends AbstractHandler {

    private final Map<String, Command> photoCommands = new HashMap<>();
    private final HashMap<Object, Command> hashMap = new HashMap<>();

    @Override
    public Answer getAnswer(final User user, final ClassifiedUpdate update) {
        log.info(user.getState().toString());
        return photoCommands.get("PHOTO").getAnswer(update, user);
    }

    // Methods to register photo commands
    public void registerPhotoCommand(final String name, final Command command) {
        photoCommands.put(name, command);
    }

    @Override
    protected HashMap<Object, Command> createMap() {
        log.info(String.valueOf(hashMap));
        return hashMap;
    }

    @Override
    public TelegramType getHandleType() {
        return TelegramType.PHOTO;
    }

    @Override
    public int priority() {
        return 1;
    }

    @Override
    public boolean condition(final User user, final ClassifiedUpdate update) {
        return TelegramType.PHOTO.equals(update.getTelegramType());
    }
}
