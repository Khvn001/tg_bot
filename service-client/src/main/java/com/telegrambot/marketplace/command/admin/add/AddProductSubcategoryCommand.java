package com.telegrambot.marketplace.command.admin.add;

import com.telegrambot.marketplace.command.admin.AdminCommand;
import com.telegrambot.marketplace.dto.bot.Answer;
import com.telegrambot.marketplace.dto.bot.ClassifiedUpdate;
import com.telegrambot.marketplace.entity.product.description.ProductCategory;
import com.telegrambot.marketplace.entity.product.description.ProductSubcategory;
import com.telegrambot.marketplace.entity.user.User;
import com.telegrambot.marketplace.enums.ProductSubcategoryName;
import com.telegrambot.marketplace.enums.UserType;
import com.telegrambot.marketplace.dto.bot.SendMessageBuilder;
import com.telegrambot.marketplace.service.entity.ProductCategoryService;
import com.telegrambot.marketplace.service.entity.ProductSubcategoryService;
import com.telegrambot.marketplace.config.typehandlers.CommandHandler;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class AddProductSubcategoryCommand implements AdminCommand {

    private final ProductSubcategoryService productSubcategoryService;
    private final ProductCategoryService productCategoryService;

    private static final int ARGS_SIZE = 2;

    @Override
    public Class handler() {
        return CommandHandler.class;
    }

    @Override
    public Object getFindBy() {
        return "/admin_add_product_subcategory_";
    }

    @Override
    @SneakyThrows
    public Answer getAnswer(final ClassifiedUpdate update, final User user) {
        if (!UserType.ADMIN.equals(user.getPermissions())) {
            return new SendMessageBuilder()
                    .chatId(user.getChatId())
                    .message("You do not have permission to add product subcategories.")
                    .build();
        }

        String[] args = update.getArgs().toArray(new String[0]);
        if (args.length < ARGS_SIZE) {
            return new SendMessageBuilder()
                    .chatId(user.getChatId())
                    .message("Usage: /admin_add_product_subcategory_ <category> <subcategory>")
                    .build();
        }

        String categoryName = args[0].toUpperCase();
        String subcategoryName = args[1].toUpperCase();

        try {
            ProductCategory category = productCategoryService.findByName(categoryName);
            if (category == null) {
                return new SendMessageBuilder()
                        .chatId(user.getChatId())
                        .message("Category not found.")
                        .build();
            }

            ProductSubcategory subcategory = productSubcategoryService.findByName(subcategoryName);
            if (subcategory != null) {
                return new SendMessageBuilder()
                        .chatId(user.getChatId())
                        .message("Subcategory already exists.")
                        .build();
            }

            subcategory = new ProductSubcategory();
            subcategory.setProductCategory(category);
            subcategory.setName(ProductSubcategoryName.valueOf(subcategoryName));
            subcategory.setAllowed(true);
            ProductSubcategory savedProductSubcategory = productSubcategoryService.save(subcategory);
            log.info("Added productSubcategory '{}' to '{}'", savedProductSubcategory, category.getName());
            return new SendMessageBuilder()
                    .chatId(user.getChatId())
                    .message("Product Subcategory " + savedProductSubcategory.getName()
                            + " added successfully to category "
                            + categoryName + ".")
                    .build();
        } catch (IllegalArgumentException e) {
            return new SendMessageBuilder()
                    .chatId(user.getChatId())
                    .message("Invalid product subcategory name: " + subcategoryName)
                    .build();
        }
    }
}
