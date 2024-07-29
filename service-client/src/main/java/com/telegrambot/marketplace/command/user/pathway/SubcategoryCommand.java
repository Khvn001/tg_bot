package com.telegrambot.marketplace.command.user.pathway;

import com.telegrambot.marketplace.command.Command;
import com.telegrambot.marketplace.config.typehandlers.CallbackHandler;
import com.telegrambot.marketplace.dto.Answer;
import com.telegrambot.marketplace.entity.inventory.ProductInventoryCity;
import com.telegrambot.marketplace.entity.location.City;
import com.telegrambot.marketplace.entity.location.Country;
import com.telegrambot.marketplace.entity.product.description.Product;
import com.telegrambot.marketplace.entity.product.description.ProductCategory;
import com.telegrambot.marketplace.entity.product.description.ProductSubcategory;
import com.telegrambot.marketplace.entity.user.User;
import com.telegrambot.marketplace.enums.CountryName;
import com.telegrambot.marketplace.enums.ProductCategoryName;
import com.telegrambot.marketplace.enums.ProductSubcategoryName;
import com.telegrambot.marketplace.enums.UserType;
import com.telegrambot.marketplace.dto.SendMessageBuilder;
import com.telegrambot.marketplace.service.entity.CityService;
import com.telegrambot.marketplace.service.entity.CountryService;
import com.telegrambot.marketplace.service.entity.ProductCategoryService;
import com.telegrambot.marketplace.service.entity.ProductInventoryCityService;
import com.telegrambot.marketplace.service.entity.ProductSubcategoryService;
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
public class SubcategoryCommand implements Command {

    private final CityService cityService;
    private final CountryService countryService;
    private final ProductInventoryCityService productInventoryCityService;
    private final ProductCategoryService productCategoryService;
    private final ProductSubcategoryService productSubcategoryService;

    private static final int ZERO_NUMBER = 0;
    private static final int ONE_NUMBER = 1;
    private static final int TWO_NUMBER = 2;
    private static final int THREE_NUMBER = 3;

    @Override
    public Class handler() {
        return CallbackHandler.class;
    }

    @Override
    public Object getFindBy() {
        return "/subcategory_";
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
        ProductSubcategoryName subcategoryName = ProductSubcategoryName.valueOf(parts[ZERO_NUMBER].toUpperCase());
        ProductCategoryName categoryName = ProductCategoryName.valueOf(parts[ONE_NUMBER].toUpperCase());
        Long cityId = Long.parseLong(parts[TWO_NUMBER]);
        CountryName countryName = CountryName.valueOf(parts[THREE_NUMBER].toUpperCase());
        City city = cityService.findById(cityId);
        ProductCategory category = productCategoryService.findByName(categoryName.toString());
        ProductSubcategory subcategory = productSubcategoryService.findByName(subcategoryName.toString());

        Map<Product, List<ProductInventoryCity>> availableProducts = productInventoryCityService
                .findAvailableProductBySubcategoryAndCategory(city, subcategory, category);

        if (availableProducts.isEmpty()) {
            return new SendMessageBuilder()
                    .chatId(user.getChatId())
                    .message("No products available in this subcategory.")
                    .build();
        }

        return new SendMessageBuilder()
                .chatId(user.getChatId())
                .message("Available products in " + subcategory.getName() + " subcategory:")
                .buttons(getProductButtons(availableProducts.keySet(), subcategory,
                        category, cityId, countryName))
                .build();
    }

    private List<InlineKeyboardButton> getProductButtons(final Set<Product> products,
                                                         final ProductSubcategory subcategory,
                                                         final ProductCategory category,
                                                         final Long cityId,
                                                         final CountryName countryName) {
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        Country country = countryService.findByCountryName(countryName);
        buttons.add(InlineKeyboardButton.builder()
                .text("Change Country")
                .callbackData("/start")
                .build());
        buttons.add(InlineKeyboardButton
                .builder()
                .text("Change City")
                .callbackData("/country_" + country.getName())
                .build());
        buttons.add(InlineKeyboardButton.builder()
                .text("Change Category")
                .callbackData("/city_" + cityId + "_" + country.getName())
                .build());
        buttons.add(InlineKeyboardButton.builder()
                .text("Change Subcategory")
                .callbackData("/category_" + category.getName() + "_" + cityId + "_" + country.getName())
                .build());
        buttons.add(InlineKeyboardButton.builder()
                .text("View Basket")
                .callbackData("/basket_")
                .build());
        buttons.add(InlineKeyboardButton.builder()
                .text("View Profile")
                .callbackData("/profile_")
                .build());
        buttons.add(InlineKeyboardButton.builder()
                .text("Add Balance")
                .callbackData("/add_balance_")
                .build());
        for (Product product : products) {
            buttons.add(InlineKeyboardButton.builder()
                    .text(product.getName())
                    .callbackData("/product_" + product.getId() + "_" + subcategory.getName()
                            + "_" + category.getName() + "_" + cityId + "_" + countryName)
                    .build());
        }
        return buttons;
    }
}
