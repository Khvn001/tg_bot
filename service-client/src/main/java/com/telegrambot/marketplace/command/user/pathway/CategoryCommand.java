package com.telegrambot.marketplace.command.user.pathway;

import com.telegrambot.marketplace.command.Command;
import com.telegrambot.marketplace.config.CallbackHandler;
import com.telegrambot.marketplace.dto.Answer;
import com.telegrambot.marketplace.entity.inventory.ProductInventoryCity;
import com.telegrambot.marketplace.entity.location.City;
import com.telegrambot.marketplace.entity.product.description.ProductCategory;
import com.telegrambot.marketplace.entity.product.description.ProductSubcategory;
import com.telegrambot.marketplace.entity.user.User;
import com.telegrambot.marketplace.enums.CountryName;
import com.telegrambot.marketplace.enums.UserType;
import com.telegrambot.marketplace.service.SendMessageBuilder;
import com.telegrambot.marketplace.service.entity.CityService;
import com.telegrambot.marketplace.service.entity.ProductCategoryService;
import com.telegrambot.marketplace.service.entity.ProductInventoryCityService;
import com.telegrambot.marketplace.service.entity.ProductSubcategoryService;
import com.telegrambot.marketplace.dto.ClassifiedUpdate;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@AllArgsConstructor
public class CategoryCommand implements Command {
    private static final Logger log = LoggerFactory.getLogger(CategoryCommand.class);
    private final CityService cityService;
    private final ProductInventoryCityService productInventoryCityService;
    private final ProductCategoryService productCategoryService;
    private final ProductSubcategoryService productSubcategoryService;

    private static final int ZERO_NUMBER = 0;
    private static final int ONE_NUMBER = 1;
    private static final int TWO_NUMBER = 2;

    @Override
    public Class handler() {
        return CallbackHandler.class;
    }

    @Override
    public Object getFindBy() {
        return "/category_";
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

        String[] parts = update.getArgs().toArray(new String[0]);
        ProductCategory category = productCategoryService.findByName(parts[ZERO_NUMBER].toUpperCase());
        Long cityId = Long.parseLong(parts[ONE_NUMBER]);
        CountryName countryName = CountryName.valueOf(parts[TWO_NUMBER].toUpperCase());
        City city = cityService.findById(cityId);

        Map<ProductSubcategory, List<ProductInventoryCity>> availableSubcategories = productInventoryCityService
                .findAvailableProductSubcategoriesByCategory(city, category);
        log.info(availableSubcategories.keySet().toString());

        return new SendMessageBuilder()
                .chatId(user.getChatId())
                .message("Available subcategories in " + category.getName().name() + " category:")
                .buttons(
                        getProductButtons(
                        availableSubcategories.keySet(), category.getName().name(), cityId, countryName))
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
