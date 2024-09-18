package com.telegrambot.marketplace.command.user.basket;

import com.telegrambot.marketplace.command.Command;
import com.telegrambot.marketplace.config.typehandlers.CallbackHandler;
import com.telegrambot.marketplace.dto.bot.Answer;
import com.telegrambot.marketplace.dto.bot.ClassifiedUpdate;
import com.telegrambot.marketplace.entity.order.Basket;
import com.telegrambot.marketplace.entity.order.Order;
import com.telegrambot.marketplace.entity.user.User;
import com.telegrambot.marketplace.enums.UserType;
import com.telegrambot.marketplace.dto.bot.SendMessageBuilder;
import com.telegrambot.marketplace.service.entity.BasketService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class BasketCommand implements Command {

    private final BasketService basketService;

    @Override
    public Class handler() {
        return CallbackHandler.class;
    }

    @Override
    public Object getFindBy() {
        return "/basket_";
    }

    @SneakyThrows
    @Override
    public Answer getAnswer(final ClassifiedUpdate update, final User user) {
        if (UserType.ADMIN.equals(user.getPermissions())
                || UserType.COURIER.equals(user.getPermissions())
                || UserType.MODERATOR.equals(user.getPermissions())) {
            return new SendMessageBuilder()
                    .chatId(user.getChatId())
                    .message("You do not have permission.")
                    .build();
        }

        String message = generateBasketContentMessage(user);
        return new SendMessageBuilder()
                .chatId(user.getChatId())
                .message(message)
                .buttons(getBasketOptionsButtons(user))
                .build();
    }

    private String generateBasketContentMessage(final User user) {
        Basket basket = basketService.getBasketByUser(user);
        BigDecimal basketTotal = BigDecimal.ZERO;
        StringBuilder message = new StringBuilder("Your basket contains:\n");
        for (Order order : basket.getOrders()) {
            message.append("\n");
            message.append("Order ID: ").append(order.getId()).append("\n");
            message.append("Product Category: ").append(order.getProductSubcategory().getName()).append("\n");
            message.append("Product: ").append(order.getProduct().getName()).append("\n");
            message.append("Amount: ").append(order.getAmount()).append("\n");
            message.append("Price: ").append(order.getPrice()).append("\n");
            message.append("Total: ").append(order.getTotalSum()).append("\n");
            basketTotal = basketTotal.add(order.getTotalSum());
            message.append("\n");
        }
        message.append("Basket Total: ").append(basketTotal).append("\n");
        return message.toString();
    }

    private List<InlineKeyboardButton> getBasketOptionsButtons(final User user) {
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(InlineKeyboardButton.builder()
                .text("Spend balance to buy basket")
                .callbackData("/buyBasket_")
                .build());

        buttons.add(InlineKeyboardButton.builder()
                .text("Monitor orders")
                .callbackData("/orders_")
                .build());

        buttons.add(InlineKeyboardButton.builder()
                .text("Add balance")
                .callbackData("/addBalance_")
                .build());

        buttons.add(InlineKeyboardButton.builder()
                .text("Change Country")
                .callbackData("/start")
                .build());
        if (user.getCountry() != null) {
            buttons.add(InlineKeyboardButton
                    .builder()
                    .text("Change City")
                    .callbackData("/country_" + user.getCountry().getName())
                    .build());
        }
        if (user.getCountry() != null && user.getCity() != null) {
            buttons.add(InlineKeyboardButton.builder()
                    .text("Change Category")
                    .callbackData("/city_" + user.getCity().getId() + "_" + user.getCountry().getName())
                    .build());
        }

        return buttons;
    }

}
