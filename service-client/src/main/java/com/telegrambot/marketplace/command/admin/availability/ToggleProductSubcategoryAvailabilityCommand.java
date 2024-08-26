package com.telegrambot.marketplace.command.admin.availability;

import com.telegrambot.marketplace.command.admin.AdminCommand;
import com.telegrambot.marketplace.dto.bot.Answer;
import com.telegrambot.marketplace.dto.bot.ClassifiedUpdate;
import com.telegrambot.marketplace.entity.product.description.ProductSubcategory;
import com.telegrambot.marketplace.entity.user.User;
import com.telegrambot.marketplace.enums.UserType;
import com.telegrambot.marketplace.dto.bot.SendMessageBuilder;
import com.telegrambot.marketplace.service.entity.ProductSubcategoryService;
import com.telegrambot.marketplace.config.typehandlers.CommandHandler;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class ToggleProductSubcategoryAvailabilityCommand implements AdminCommand {

    private final ProductSubcategoryService productSubcategoryService;

    @Override
    public Class handler() {
        return CommandHandler.class;
    }

    @Override
    public Object getFindBy() {
        return "/admin_toggle_subcategory_availability_";
    }

    @Override
    @SneakyThrows
    public Answer getAnswer(final ClassifiedUpdate update, final User user) {
        if (!UserType.ADMIN.equals(user.getPermissions())) {
            return new SendMessageBuilder()
                    .chatId(user.getChatId())
                    .message("You do not have permission to toggle subcategory availability.")
                    .build();
        }

        String[] args = update.getArgs().toArray(new String[0]);
        if (args.length < 1) {
            return new SendMessageBuilder()
                    .chatId(user.getChatId())
                    .message("Usage: /admin_toggle_subcategory_availability_ <subcategory>")
                    .build();
        }

        String subcategoryName = args[0];
        ProductSubcategory subcategory = productSubcategoryService.findByName(subcategoryName);
        if (subcategoryName == null) {
            return new SendMessageBuilder()
                    .chatId(user.getChatId())
                    .message("Subcategory not found.")
                    .build();
        }

        subcategory.setAllowed(!subcategory.isAllowed());
        productSubcategoryService.save(subcategory);

        String status = subcategory.isAllowed() ? "available" : "unavailable";

        log.info("Subcategory: {}. New Status: {}", subcategory.getName(), status);

        return new SendMessageBuilder()
                .chatId(user.getChatId())
                .message("Subcategory " + subcategoryName + " is now " + status + ".")
                .build();
    }
}
