package com.telegrambot.marketplace.command.admin.add;

import com.telegrambot.marketplace.command.admin.AdminCommand;
import com.telegrambot.marketplace.dto.bot.Answer;
import com.telegrambot.marketplace.dto.bot.ClassifiedUpdate;
import com.telegrambot.marketplace.entity.product.description.ProductCategory;
import com.telegrambot.marketplace.entity.user.User;
import com.telegrambot.marketplace.enums.ProductCategoryName;
import com.telegrambot.marketplace.enums.UserType;
import com.telegrambot.marketplace.dto.bot.SendMessageBuilder;
import com.telegrambot.marketplace.service.entity.ProductCategoryService;
import com.telegrambot.marketplace.config.typehandlers.CommandHandler;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class AddProductCategoryCommand implements AdminCommand {

    private final ProductCategoryService productCategoryService;

    private static final int ARGS_SIZE = 1;

    @Override
    public Class handler() {
        return CommandHandler.class;
    }

    @Override
    public Object getFindBy() {
        return "/admin_add_product_category_";
    }

    @Override
    @SneakyThrows
    public Answer getAnswer(final ClassifiedUpdate update, final User user) {
        if (!UserType.ADMIN.equals(user.getPermissions())) {
            return new SendMessageBuilder()
                    .chatId(user.getChatId())
                    .message("You do not have permission to add product categories.")
                    .build();
        }

        String[] args = update.getArgs().toArray(new String[0]);
        if (args.length < ARGS_SIZE) {
            return new SendMessageBuilder()
                    .chatId(user.getChatId())
                    .message("Usage: /admin_add_product_category_ <category>")
                    .build();
        }

        String categoryNameString = args[0].toUpperCase();

        ProductCategoryName categoryName;
        try {
            categoryName = ProductCategoryName.valueOf(categoryNameString);
        } catch (IllegalArgumentException e) {
            return new SendMessageBuilder()
                    .chatId(user.getChatId())
                    .message("Invalid product category name: " + categoryNameString)
                    .build();
        }

        ProductCategory category = productCategoryService.findByName(categoryName.toString());
        if (category != null) {
            return new SendMessageBuilder()
                    .chatId(user.getChatId())
                    .message("Category already exists.")
                    .build();
        }

        category = new ProductCategory();
        category.setName(categoryName);
        category.setAllowed(true);
        ProductCategory savedProductCategory = productCategoryService.save(category);
        log.info("Added product category '{}'", savedProductCategory);
        return new SendMessageBuilder()
                .chatId(user.getChatId())
                .message("Product category " + savedProductCategory.getName()
                        + " added successfully" + ".")
                .build();

    }
}

