package com.telegrambot.marketplace.command.user.pathway;

import com.telegrambot.marketplace.command.Command;
import com.telegrambot.marketplace.config.typehandlers.CallbackHandler;
import com.telegrambot.marketplace.dto.bot.Answer;
import com.telegrambot.marketplace.dto.bot.ClassifiedUpdate;
import com.telegrambot.marketplace.entity.location.Country;
import com.telegrambot.marketplace.entity.location.District;
import com.telegrambot.marketplace.entity.product.description.Product;
import com.telegrambot.marketplace.entity.product.description.ProductCategory;
import com.telegrambot.marketplace.entity.product.description.ProductSubcategory;
import com.telegrambot.marketplace.entity.user.User;
import com.telegrambot.marketplace.enums.CountryName;
import com.telegrambot.marketplace.enums.ProductCategoryName;
import com.telegrambot.marketplace.enums.ProductSubcategoryName;
import com.telegrambot.marketplace.enums.StateType;
import com.telegrambot.marketplace.enums.UserType;
import com.telegrambot.marketplace.dto.bot.SendMessageBuilder;
import com.telegrambot.marketplace.service.entity.CountryService;
import com.telegrambot.marketplace.service.entity.DistrictService;
import com.telegrambot.marketplace.service.entity.ProductCategoryService;
import com.telegrambot.marketplace.service.entity.ProductService;
import com.telegrambot.marketplace.service.entity.ProductSubcategoryService;
import com.telegrambot.marketplace.service.entity.StateService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class DistrictCommand implements Command {

    private final DistrictService districtService;
    private final ProductService productService;
    private final ProductCategoryService productCategoryService;
    private final ProductSubcategoryService productSubcategoryService;
    private final CountryService countryService;
    private final StateService stateService;

    private static final int ZERO_NUMBER = 0;
    private static final int ONE_NUMBER = 1;
    private static final int TWO_NUMBER = 2;
    private static final int THREE_NUMBER = 3;
    private static final int FOUR_NUMBER = 4;
    private static final int FIVE_NUMBER = 5;

    @Override
    public Class handler() {
        return CallbackHandler.class;
    }

    @Override
    public Object getFindBy() {
        return "/district_";
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
        Long districtId = Long.parseLong(parts[ZERO_NUMBER]);
        District district = districtService.findById(districtId);
        Long productId = Long.parseLong(parts[ONE_NUMBER]);
        Product product = productService.findById(productId);
        ProductSubcategoryName subcategoryName = ProductSubcategoryName.valueOf(parts[TWO_NUMBER].toUpperCase());
        ProductSubcategory subcategory = productSubcategoryService.findByNameAndAllowedTrue(String.valueOf(subcategoryName));
        ProductCategoryName categoryName = ProductCategoryName.valueOf(parts[THREE_NUMBER].toUpperCase());
        ProductCategory category = productCategoryService.findByNameAndAllowedTrue(String.valueOf(categoryName));
        Long cityId = Long.parseLong(parts[FOUR_NUMBER]);
        CountryName countryName = CountryName.valueOf(parts[FIVE_NUMBER].toUpperCase());
        user.getState().setStateType(StateType.ORDER);
        user.getState().setValue(String.format("%d_%d_%s_%s_%d_%s",
                districtId, productId, subcategoryName, categoryName, cityId, countryName));
        stateService.save(user.getState());
        return new SendMessageBuilder()
                .chatId(user.getChatId())
                .message("You selected " + district.getName() + ". Please ENTER the AMOUNT you want to order:")
                .buttons(getDistrictButtons(district, product, subcategory,
                        category, cityId, countryName))
                .build();
    }

    private List<InlineKeyboardButton> getDistrictButtons(final District district,
                                                         final Product product,
                                                         final ProductSubcategory subcategory,
                                                         final ProductCategory category,
                                                         final Long cityId,
                                                         final CountryName countryName) {
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        Country country = countryService.findByCountryNameAndAllowedTrue(countryName);
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
                .text("Change Product")
                .callbackData("/product_" + product.getId() + "_" + category.getName()
                        + "_" + cityId + "_" + country.getName())
                .build());
        buttons.add(InlineKeyboardButton.builder()
                .text("Change District")
                .callbackData("/district_" + district.getId() +  "_" + product.getId() + "_" + category.getName()
                        + "_" + cityId + "_" + country.getName())
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
        return buttons;
    }
}
