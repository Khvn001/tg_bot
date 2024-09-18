package com.telegrambot.marketplace.command.user.basket;

import com.telegrambot.marketplace.command.Command;
import com.telegrambot.marketplace.config.typehandlers.CallbackHandler;
import com.telegrambot.marketplace.dto.bot.Answer;
import com.telegrambot.marketplace.dto.bot.ClassifiedUpdate;
import com.telegrambot.marketplace.entity.inventory.Photo;
import com.telegrambot.marketplace.entity.inventory.ProductPortion;
import com.telegrambot.marketplace.entity.order.Basket;
import com.telegrambot.marketplace.entity.order.Order;
import com.telegrambot.marketplace.entity.user.User;
import com.telegrambot.marketplace.enums.UserType;
import com.telegrambot.marketplace.dto.bot.SendMessageBuilder;
import com.telegrambot.marketplace.service.entity.BasketService;
import com.telegrambot.marketplace.service.entity.UserService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class BuyBasketCommand implements Command {

    private final BasketService basketService;
    private final UserService userService;

    @Override
    public Class handler() {
        return CallbackHandler.class;
    }

    @Override
    public Object getFindBy() {
        return "/buyBasket_";
    }

    @SneakyThrows
    @Override
    @Transactional
    public Answer getAnswer(final ClassifiedUpdate update, final User user) {
        if (UserType.ADMIN.equals(user.getPermissions())
                || UserType.COURIER.equals(user.getPermissions())
                || UserType.MODERATOR.equals(user.getPermissions())) {
            return new SendMessageBuilder()
                    .chatId(user.getChatId())
                    .message("You do not have permission.")
                    .build();
        }

        Basket basket = basketService.getBasketByUser(user);
        boolean success;
        if (user.getBalance().compareTo(basket.getTotalSum()) >= 0) {
            user.setBalance(user.getBalance().subtract(basket.getTotalSum()));
            userService.save(user);
            StringBuilder message = new StringBuilder("Basket purchased successfully! Thank you for your order.\n");
            for (Order order : basket.getOrders()) {
                message.append("\n");
                message.append("Order ID: ").append(order.getId()).append("\n");
                message.append("Product Category: ").append(order.getProductSubcategory().getName()).append("\n");
                message.append("Product: ").append(order.getProduct().getName()).append("\n");
                for (ProductPortion productPortion : order.getProductPortions()) {
                    message.append("Amount: ").append(productPortion.getAmount());
                    message.append("Coordinates: ").append(productPortion.getLatitude()).append(",")
                            .append(productPortion.getLongitude()).append("\n");
                    message.append("Photo: ").append(productPortion.getPhotos().stream()
                                .map(Photo::getPhotoUrl)
                                .collect(Collectors.joining(", "))
                            )
                            .append("\n");
                }
                message.append("\n");
            }

            basketService.completePurchase(user);
            success = true;
            return new SendMessageBuilder()
                    .chatId(user.getChatId())
                    .message(String.valueOf(message))
                    .buttons(getBasketOptionsButtons(user, success))
                    .build();
        } else {
            success = false;
            return new SendMessageBuilder()
                    .chatId(user.getChatId())
                    .message("Insufficient balance to complete the purchase.")
                    .buttons(getBasketOptionsButtons(user, success))
                    .build();
        }
    }

    private List<InlineKeyboardButton> getBasketOptionsButtons(final User user, final boolean success) {
        List<InlineKeyboardButton> buttons = new ArrayList<>();

        buttons.add(InlineKeyboardButton.builder()
                .text("Change country:")
                .callbackData("/start")
                .build());

        if (user.getCountry() != null && user.getCity() != null) {
            buttons.add(InlineKeyboardButton.builder()
                    .text("Go back to Category selection")
                    .callbackData("/city_" + user.getCity().getId() + "_" + user.getCountry().getName())
                    .build());
        }

        if (!success) {
            buttons.add(InlineKeyboardButton.builder()
                    .text("Add Balance")
                    .callbackData("/addBalance_")
                    .build());
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
        }

        return buttons;
    }

}
