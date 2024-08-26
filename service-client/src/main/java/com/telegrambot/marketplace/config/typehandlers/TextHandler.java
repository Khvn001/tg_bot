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
public class TextHandler extends AbstractHandler {

    private final Map<String, Command> textCommands = new HashMap<>();
    private final HashMap<Object, Command> hashMap = new HashMap<>();

    @Override
    public Answer getAnswer(final User user, final ClassifiedUpdate update) {
        String command = update.getCommandName();
        log.info(command);
        log.info(user.getState().toString());
        log.info(textCommands.keySet().toString());
        log.info(textCommands.values().toString());
        return textCommands.get("TEXT").getAnswer(update, user);
    }

    // Methods to register commands
    public void registerTextCommand(final String name, final Command command) {
        textCommands.put(name, command);
    }

    @Override
    protected HashMap<Object, Command> createMap() {
        log.info(String.valueOf(hashMap));
        return hashMap;
    }

    @Override
    public TelegramType getHandleType() {
        return TelegramType.TEXT;
    }

    @Override
    public int priority() {
        return 1;
    }

    @Override
    public boolean condition(final User user, final ClassifiedUpdate update) {
        return TelegramType.TEXT.equals(update.getTelegramType());
    }
}
