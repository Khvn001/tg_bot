package com.telegrambot.marketplace.service.update;

import com.telegrambot.marketplace.enums.TelegramType;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ClassifiedUpdate {
    @Getter
    private final TelegramType telegramType; // enum, чтобы всё выглядило красиво

    @Getter
    private final Long userId; // тот же chat-id, но выглядит красивее и получить его легче

    @Getter
    private String name; // получим имя пользователя. Именно имя, не @username

    @Getter
    private String commandName; // если это команда, то запишем её

    @Getter
    private final Update update; // сохраним сам update, чтобы в случае чего, его можно было достать

    @Getter
    private final List<String> args; // просто поделим текст сообщения, в будущем это поможет

    @Getter
    private String userName; // @username

    public ClassifiedUpdate(Update update) {
        this.update = update;
        this.telegramType = handleTelegramType();
        this.userId = handleUserId();
        this.args = handleArgs();
        this.commandName = handleCommandName();
    }

    //Обработаем команду.
    public String handleCommandName() {
        if(update.hasMessage()) {
            if(update.getMessage().hasText()) {
                if(update.getMessage().getText().startsWith("/")) {
                    return update.getMessage().getText().split(" ")[0];
                } else return update.getMessage().getText();
            }
        } if(update.hasCallbackQuery()) {
            return update.getCallbackQuery().getData().split(" ")[0];
        }
        return "";
    }

    //Обработаем тип сообщения
    private TelegramType handleTelegramType() {

        if(update.hasCallbackQuery())
            return TelegramType.CALL_BACK;

        if(update.hasMessage()) {
            if(update.getMessage().hasText()) {
                if(update.getMessage().getText().startsWith("/"))
                    return TelegramType.COMMAND;
                else
                    return TelegramType.TEXT;
            } else if(update.getMessage().hasSuccessfulPayment()) {
                return TelegramType.SUCCESS_PAYMENT;
            } else if(update.getMessage().hasPhoto())
                return TelegramType.PHOTO;
        } else if(update.hasPreCheckoutQuery()) {
            return TelegramType.PRE_CHECKOUT_QUERY;
        } else if(update.hasChatJoinRequest()) {
            return TelegramType.CHAT_JOIN_REQUEST;
        } else if(update.hasChannelPost()) {
            return TelegramType.CHANNEL_POST;
        }
        else if(update.hasMyChatMember()) {
            return TelegramType.MY_CHAT_MEMBER;
        }
        if(update.getMessage().hasDocument()) {
            return TelegramType.TEXT;
        }
        return TelegramType.UNKNOWN;
    }

    //Достанем userId, имя и username из любого типа сообщений.
    private Long handleUserId() {
        if (telegramType == TelegramType.PRE_CHECKOUT_QUERY) {
            name = getNameByUser(update.getPreCheckoutQuery().getFrom());
            userName = update.getPreCheckoutQuery().getFrom().getUserName();
            return update.getPreCheckoutQuery().getFrom().getId();
        } else if(telegramType == TelegramType.CHAT_JOIN_REQUEST) {
            name = getNameByUser(update.getChatJoinRequest().getUser());
            userName = update.getChatJoinRequest().getUser().getUserName();
            return update.getChatJoinRequest().getUser().getId();
        } else if (telegramType == TelegramType.CALL_BACK) {
            name = getNameByUser(update.getCallbackQuery().getFrom());
            userName = update.getCallbackQuery().getFrom().getUserName();
            return update.getCallbackQuery().getFrom().getId();
        } else if(telegramType == TelegramType.MY_CHAT_MEMBER){
            name = update.getMyChatMember().getChat().getTitle();
            userName = update.getMyChatMember().getChat().getUserName();
            return update.getMyChatMember().getFrom().getId();
        } else {
            name = getNameByUser(update.getMessage().getFrom());
            userName = update.getMessage().getFrom().getUserName();
            return update.getMessage().getFrom().getId();
        }
    }

    //Разделим сообщение на аргументы
    private List<String> handleArgs() {
        List<String> list = new LinkedList<>();

        if(telegramType == TelegramType.COMMAND) {
            String[] args = getUpdate().getMessage().getText().split(" ");
            Collections.addAll(list, args);
            list.remove(0);

            return list;
        } else if (telegramType == TelegramType.TEXT) {
            list.add(getUpdate().getMessage().getText());

            return list;
        } else if (telegramType == TelegramType.CALL_BACK) {
            String[] args = getUpdate().getCallbackQuery().getData().split(" ");
            Collections.addAll(list, args);
            list.remove(0);

            return list;
        }
        return new ArrayList<>();
    }

    //Вынесли имя в другой метод
    private String getNameByUser(User user) {
        if(user.getIsBot())
            return "BOT";

        if(!user.getFirstName().isBlank() || !user.getFirstName().isEmpty())
            return user.getFirstName();

        if(!user.getUserName().isBlank() || !user.getUserName().isEmpty())
            return user.getUserName();

        return "noname";
    }

    //Лог
    public String getLog() {

        return "USER_ID : " + getUserId() +
                "\nUSER_NAME : " + getName() +
                "\nTYPE : " + getTelegramType() +
                "\nARGS : " + getArgs().toString() +
                "\nCOMMAND_NAME : " + getCommandName();
    }

}
