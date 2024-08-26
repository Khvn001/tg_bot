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

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class OrdersCommand implements Command {

    private final BasketService basketService;

    @Override
    public Class handler() {
        return CallbackHandler.class;
    }

    @Override
    public Object getFindBy() {
        return "/orders_";
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

        List<InlineKeyboardButton> buttons = new ArrayList<>();
        Basket basket = basketService.getBasketByUser(user);
        buttons.add(InlineKeyboardButton.builder()
                .text("Back to Basket")
                .callbackData("/basket_")
                .build());
        buttons.add(InlineKeyboardButton.builder()
                .text("View Profile")
                .callbackData("/profile_")
                .build());
        buttons.add(InlineKeyboardButton.builder()
                .text("Spend balance to buy basket")
                .callbackData("/buyBasket_")
                .build());
        buttons.add(InlineKeyboardButton.builder()
                .text("Add balance")
                .callbackData("/add_balance_")
                .build());
        buttons.add(InlineKeyboardButton.builder()
                .text("Delete All Orders")
                .callbackData("/deleteOrders_")
                .build());
        for (Order order : basket.getOrders()) {
            buttons.add(InlineKeyboardButton.builder()
                    .text("Delete Order " + order.getId() + " " + order.getProductSubcategory().getName()
                            + " " + order.getProduct().getName() + " " + order.getAmount() + " " + order.getTotalSum())
                    .callbackData("/deleteOrder_" + order.getId())
                    .build());
        }
        return new SendMessageBuilder()
                .chatId(user.getChatId())
                .message("Select an order to delete:")
                .buttons(buttons)
                .build();
    }

}
