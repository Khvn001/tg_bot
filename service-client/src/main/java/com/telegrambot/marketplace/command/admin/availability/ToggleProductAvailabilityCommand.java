package com.telegrambot.marketplace.command.admin.availability;

import com.telegrambot.marketplace.command.admin.AdminCommand;
import com.telegrambot.marketplace.dto.bot.Answer;
import com.telegrambot.marketplace.dto.bot.ClassifiedUpdate;
import com.telegrambot.marketplace.entity.product.description.Product;
import com.telegrambot.marketplace.entity.product.description.ProductSubcategory;
import com.telegrambot.marketplace.entity.user.User;
import com.telegrambot.marketplace.enums.UserType;
import com.telegrambot.marketplace.dto.bot.SendMessageBuilder;
import com.telegrambot.marketplace.service.entity.ProductService;
import com.telegrambot.marketplace.service.entity.ProductSubcategoryService;
import com.telegrambot.marketplace.config.typehandlers.CommandHandler;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class ToggleProductAvailabilityCommand implements AdminCommand {

    private final ProductService productService;
    private final ProductSubcategoryService productSubcategoryService;

    @Override
    public Class handler() {
        return CommandHandler.class;
    }

    @Override
    public Object getFindBy() {
        return "/admin_toggle_product_availability_";
    }

    @Override
    @SneakyThrows
    public Answer getAnswer(final ClassifiedUpdate update, final User user) {
        if (!UserType.ADMIN.equals(user.getPermissions())) {
            return new SendMessageBuilder()
                    .chatId(user.getChatId())
                    .message("You do not have permission to toggle product availability.")
                    .build();
        }

        String[] args = update.getArgs().toArray(new String[0]);
        if (args.length < 2) {
            return new SendMessageBuilder()
                    .chatId(user.getChatId())
                    .message("Usage: /admin_toggle_product_availability_ <subcategory> <product>")
                    .build();
        }

        String subcategoryProductName = args[0];
        ProductSubcategory productSubcategory = productSubcategoryService.findByName(subcategoryProductName);
        if (productSubcategory == null) {
            return new SendMessageBuilder()
                    .chatId(user.getChatId())
                    .message("Product Subcategory not found.")
                    .build();
        }

        String productName = args[1];
        Product product = productService.findByName(productSubcategory.getProductCategory(),
                productSubcategory,
                productName);
        if (product == null) {
            return new SendMessageBuilder()
                    .chatId(user.getChatId())
                    .message("Product not found.")
                    .build();
        }

        product.setAllowed(!product.isAllowed());
        productService.save(product);

        String status = product.isAllowed() ? "available" : "unavailable";

        log.info("Product: {}. New Status: {}", product.getName(), status);

        return new SendMessageBuilder()
                .chatId(user.getChatId())
                .message("Product " + productName + " is now " + status + ".")
                .build();
    }
}
