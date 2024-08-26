package com.telegrambot.marketplace.command.admin.add;

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

import java.math.BigDecimal;

@Component
@AllArgsConstructor
@Slf4j
public class AddProductCommand implements AdminCommand {

    private final ProductService productService;
    private final ProductSubcategoryService productSubcategoryService;

    private static final int ARGS_SIZE = 3;

    @Override
    public Class handler() {
        return CommandHandler.class;
    }

    @Override
    public Object getFindBy() {
        return "/admin_add_product_";
    }

    @Override
    @SneakyThrows
    public Answer getAnswer(final ClassifiedUpdate update, final User user) {
        if (!UserType.ADMIN.equals(user.getPermissions())) {
            return new SendMessageBuilder()
                    .chatId(user.getChatId())
                    .message("You do not have permission to add products.")
                    .build();
        }

        String[] args = update.getArgs().toArray(new String[0]);
        if (args.length < ARGS_SIZE) {
            return new SendMessageBuilder()
                    .chatId(user.getChatId())
                    .message("Usage: /admin_add_product_ <subcategory> <name> <price>")
                    .build();
        }

        String subcategoryName = args[0].toUpperCase();
        String productName = args[1];
        double price;
        try {
            price = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            return new SendMessageBuilder()
                    .chatId(user.getChatId())
                    .message("Invalid price format.")
                    .build();
        }

        Product product = new Product();
        product.setName(productName);
        product.setAllowed(true);

        try {
            ProductSubcategory subcategory = productSubcategoryService.findByName(subcategoryName);
            if (subcategory == null) {
                return new SendMessageBuilder()
                        .chatId(user.getChatId())
                        .message("Subcategory not found.")
                        .build();
            }

            product.setProductSubcategory(subcategory);
            product.setProductCategory(subcategory.getProductCategory());

        } catch (IllegalArgumentException e) {
            return new SendMessageBuilder()
                    .chatId(user.getChatId())
                    .message("Invalid product subcategory name: " + subcategoryName)
                    .build();
        }

        product.setDescription("");
        product.setPhotoUrl("");
        product.setPrice(BigDecimal.valueOf(price));
        productService.save(product);

        log.info("Added product '{}' to '{}'", productName, subcategoryName);

        return new SendMessageBuilder()
                .chatId(user.getChatId())
                .message("Product " + productName + " added successfully to category " + subcategoryName + ".")
                .build();
    }
}
