package com.telegrambot.marketplace.command.admin.availability;

import com.telegrambot.marketplace.command.admin.AdminCommand;
import com.telegrambot.marketplace.dto.bot.Answer;
import com.telegrambot.marketplace.dto.bot.ClassifiedUpdate;
import com.telegrambot.marketplace.entity.product.description.ProductCategory;
import com.telegrambot.marketplace.entity.user.User;
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
public class ToggleProductCategoryAvailabilityCommand implements AdminCommand {

    private final ProductCategoryService productCategoryService;

    @Override
    public Class handler() {
        return CommandHandler.class;
    }

    @Override
    public Object getFindBy() {
        return "/admin_toggle_category_availability_";
    }

    @Override
    @SneakyThrows
    public Answer getAnswer(final ClassifiedUpdate update, final User user) {
        if (!UserType.ADMIN.equals(user.getPermissions())) {
            return new SendMessageBuilder()
                    .chatId(user.getChatId())
                    .message("You do not have permission to toggle category availability.")
                    .build();
        }

        String[] args = update.getArgs().toArray(new String[0]);
        if (args.length < 1) {
            return new SendMessageBuilder()
                    .chatId(user.getChatId())
                    .message("Usage: /admin_toggle_category_availability_ <category>")
                    .build();
        }

        String categoryName = args[0];
        ProductCategory category = productCategoryService.findByName(categoryName);
        if (category == null) {
            return new SendMessageBuilder()
                    .chatId(user.getChatId())
                    .message("Category not found.")
                    .build();
        }

        category.setAllowed(!category.isAllowed());
        productCategoryService.save(category);

        String status = category.isAllowed() ? "available" : "unavailable";

        log.info("Category: {}. New Status: {}", category.getName(), status);

        return new SendMessageBuilder()
                .chatId(user.getChatId())
                .message("Category " + categoryName + " is now " + status + ".")
                .build();
    }
}
