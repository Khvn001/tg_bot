package com.telegrambot.marketplace.command.user.profile;

import com.telegrambot.marketplace.command.Command;
import com.telegrambot.marketplace.config.typehandlers.CallbackHandler;
import com.telegrambot.marketplace.dto.Answer;
import com.telegrambot.marketplace.dto.ClassifiedUpdate;
import com.telegrambot.marketplace.entity.user.User;
import com.telegrambot.marketplace.enums.UserType;
import com.telegrambot.marketplace.dto.SendMessageBuilder;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class ProfileInfoViewCommand implements Command {

    @Override
    public Class handler() {
        return CallbackHandler.class;
    }

    @Override
    public Object getFindBy() {
        return "/profile_";
    }

    @Override
    @SneakyThrows
    public Answer getAnswer(final ClassifiedUpdate update, final User user) {
        if (UserType.ADMIN.equals(user.getPermissions())
                || UserType.COURIER.equals(user.getPermissions())
                || UserType.MODERATOR.equals(user.getPermissions())) {
            return new SendMessageBuilder()
                    .chatId(user.getChatId())
                    .message("You do not have permission.")
                    .build();
        }

        String message = generateProfileContentMessage(user);

        List<InlineKeyboardButton> buttons = new ArrayList<>(getButtons(user));
        return new SendMessageBuilder()
                .chatId(user.getChatId())
                .message(message)
                .buttons(buttons)
                .build();
    }

    private String generateProfileContentMessage(final User user) {
        return "Your profile information:\n" + "Name: " + user.getName() + "\n" +
                "Balance: " + user.getBalance() + "\n" +
                "hashName: " + user.getChatId() + "\n" +
                "Personal Discount: " + user.getDiscount() + "\n" +
                "Number of referrals: " + (long) user.getReferrals().size() + "\n";
    }

    private List<InlineKeyboardButton> getButtons(final User user) {
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(InlineKeyboardButton.builder()
                .text("View Basket")
                .callbackData("/basket_")
                .build());
        if (!user.getBasket().getOrders().isEmpty()) {
            buttons.add(InlineKeyboardButton.builder()
                    .text("View Orders")
                    .callbackData("/orders_")
                    .build());
        }

        buttons.add(InlineKeyboardButton.builder()
                .text("Change Country")
                .callbackData("/start")
                .build());


        if (user.getCountry() != null && user.getCity() != null) {
            buttons.add(InlineKeyboardButton.builder()
                    .text("Go back to category selection")
                    .callbackData("/city_" + user.getCity().getId() + "_" + user.getCountry().getName())
                    .build());
        }

        buttons.add(InlineKeyboardButton.builder()
                .text("Add Balance")
                .callbackData("/add_balance_")
                .build());

        return buttons;
    }
}
