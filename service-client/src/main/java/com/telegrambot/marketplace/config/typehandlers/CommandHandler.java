package com.telegrambot.marketplace.config.typehandlers;

import com.telegrambot.marketplace.command.admin.AdminCommand;
import com.telegrambot.marketplace.dto.bot.Answer;
import com.telegrambot.marketplace.entity.user.User;
import com.telegrambot.marketplace.enums.TelegramType;
import com.telegrambot.marketplace.dto.bot.ClassifiedUpdate;
import com.telegrambot.marketplace.command.Command;
import com.telegrambot.marketplace.enums.UserType;
import com.telegrambot.marketplace.dto.bot.SendMessageBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
@AllArgsConstructor
@Slf4j
public class CommandHandler extends AbstractHandler {

    private final Map<String, AdminCommand> adminCommands = new HashMap<>();
    private final Map<String, Command> userCommands = new HashMap<>();
    private final HashMap<Object, Command> hashMap = new HashMap<>();

    @Override
    public Answer getAnswer(final User user, final ClassifiedUpdate update) {
        String command = update.getCommandName();
        log.info(command);
        if (user != null && UserType.ADMIN.equals(user.getPermissions())) {
            AdminCommand adminCommand = adminCommands.get(command);
            if (adminCommand != null) {
                return adminCommand.getAnswer(update, user);
            } else if (Objects.equals(command, "/start")) {
                return userCommands.get("/start").getAnswer(update, user);
            } else {
                return new SendMessageBuilder()
                        .chatId(user.getChatId())
                        .message("Unknown command or You do not have permission.")
                        .build();
            }
        } else {
            Command userCommand = userCommands.get(command);
            if (userCommand != null) {
                return userCommand.getAnswer(update, user);
            } else {
                return new SendMessageBuilder()
                        .chatId(user.getChatId())
                        .message("Unknown command or You do not have permission.")
                        .build();
            }
        }
    }

    // Methods to register commands
    public void registerUserCommand(final String name, final Command command) {
        userCommands.put(name, command);
    }

    public void registerAdminCommand(final String name, final AdminCommand command) {
        adminCommands.put(name, command);
    }

    @Override
    protected HashMap<Object, Command> createMap() {
        log.info(String.valueOf(hashMap));
        return hashMap;
    }

    @Override
    public TelegramType getHandleType() {
        return TelegramType.COMMAND;
    }

    @Override
    public int priority() {
        return 1;
    }

    @Override
    public boolean condition(final User user, final ClassifiedUpdate update) {
        return hashMap.containsKey(update.getCommandName());
    }

}
