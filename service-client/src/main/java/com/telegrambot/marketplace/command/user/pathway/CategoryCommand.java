package com.telegrambot.marketplace.command.user.pathway;

import com.telegrambot.marketplace.command.Command;
import com.telegrambot.marketplace.dto.Answer;
import com.telegrambot.marketplace.entity.inventory.ProductInventoryCity;
import com.telegrambot.marketplace.entity.location.City;
import com.telegrambot.marketplace.entity.product.description.ProductCategory;
import com.telegrambot.marketplace.entity.product.description.ProductSubcategory;
import com.telegrambot.marketplace.entity.user.User;
import com.telegrambot.marketplace.enums.CountryName;
import com.telegrambot.marketplace.service.SendMessageBuilder;
import com.telegrambot.marketplace.service.entity.CityService;
import com.telegrambot.marketplace.service.entity.ProductCategoryService;
import com.telegrambot.marketplace.service.entity.ProductInventoryCityService;
import com.telegrambot.marketplace.service.entity.ProductSubcategoryService;
import com.telegrambot.marketplace.service.handler.CommandHandler;
import com.telegrambot.marketplace.dto.ClassifiedUpdate;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@AllArgsConstructor
public class CategoryCommand implements Command {
    private final CityService cityService;
    private final ProductInventoryCityService productInventoryCityService;
    private final ProductCategoryService productCategoryService;
    private final ProductSubcategoryService productSubcategoryService;

    private static final int ONE_NUMBER = 1;
    private static final int TWO_NUMBER = 2;
    private static final int THREE_NUMBER = 3;

    @Override
    public Class handler() {
        return CommandHandler.class;
    }

    @Override
    public Object getFindBy() {
        return "/category_";
    }

    @SneakyThrows
    @Override
    public Answer getAnswer(final ClassifiedUpdate update, final User user) {
        String[] parts = update.getCommandName().split("_");
        ProductCategory category = productCategoryService.findByName(parts[ONE_NUMBER]);
        Long cityId = Long.parseLong(parts[TWO_NUMBER]);
        CountryName countryName = CountryName.valueOf(parts[THREE_NUMBER]);
        City city = cityService.findById(cityId);

        Map<ProductSubcategory, List<ProductInventoryCity>> availableSubcategories = productInventoryCityService
                .findAvailableProductSubcategoriesByCategory(city, category);

        return new SendMessageBuilder()
                .chatId(user.getChatId())
                .message("Available subcategories in " + category + " category:")
                .buttons(
                        getProductButtons(
                        availableSubcategories.keySet(), String.valueOf(category.getName()), cityId, countryName))
                .build();
    }

    private List<InlineKeyboardButton> getProductButtons(
            final Set<ProductSubcategory> productSubcategories, final String categoryName,
            final Long cityId, final CountryName countryName) {
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        for (ProductSubcategory productSubcategory : productSubcategories) {
            buttons.add(InlineKeyboardButton.builder()
                    .text(String.valueOf(productSubcategory.getName()))
                    .callbackData("/subcategory_" + productSubcategory.getName() + "_" + categoryName
                            + "_" + cityId + "_" + countryName)
                    .build());
        }
        return buttons;
    }
}
