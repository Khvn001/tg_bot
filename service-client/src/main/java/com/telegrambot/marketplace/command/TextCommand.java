package com.telegrambot.marketplace.command;

import com.telegrambot.marketplace.config.typehandlers.TextHandler;
import com.telegrambot.marketplace.dto.bot.Answer;
import com.telegrambot.marketplace.dto.bot.ClassifiedUpdate;
import com.telegrambot.marketplace.entity.inventory.ProductPortion;
import com.telegrambot.marketplace.entity.location.City;
import com.telegrambot.marketplace.entity.location.Country;
import com.telegrambot.marketplace.entity.location.District;
import com.telegrambot.marketplace.entity.order.Order;
import com.telegrambot.marketplace.entity.product.description.Product;
import com.telegrambot.marketplace.entity.product.description.ProductCategory;
import com.telegrambot.marketplace.entity.product.description.ProductSubcategory;
import com.telegrambot.marketplace.entity.user.State;
import com.telegrambot.marketplace.entity.user.User;
import com.telegrambot.marketplace.enums.CountryName;
import com.telegrambot.marketplace.enums.ProductCategoryName;
import com.telegrambot.marketplace.enums.ProductSubcategoryName;
import com.telegrambot.marketplace.enums.StateType;
import com.telegrambot.marketplace.enums.UserType;
import com.telegrambot.marketplace.repository.ProductPortionRepository;
import com.telegrambot.marketplace.dto.bot.SendMessageBuilder;
import com.telegrambot.marketplace.service.entity.BasketService;
import com.telegrambot.marketplace.service.entity.CityService;
import com.telegrambot.marketplace.service.entity.CountryService;
import com.telegrambot.marketplace.service.entity.DistrictService;
import com.telegrambot.marketplace.service.entity.OrderService;
import com.telegrambot.marketplace.service.entity.ProductCategoryService;
import com.telegrambot.marketplace.service.entity.ProductPortionService;
import com.telegrambot.marketplace.service.entity.ProductService;
import com.telegrambot.marketplace.service.entity.ProductSubcategoryService;
import com.telegrambot.marketplace.service.entity.StateService;
import com.telegrambot.marketplace.service.entity.UserService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@AllArgsConstructor
@Slf4j
public class TextCommand implements Command {

    private final UserService userService;
    private final StateService stateService;
    private final CountryService countryService;
    private final ProductPortionService productPortionService;
    private final OrderService orderService;
    private final BasketService basketService;
    private final DistrictService districtService;
    private final ProductService productService;
    private final CityService cityService;
    private final ProductCategoryService productCategoryService;
    private final ProductSubcategoryService productSubcategoryService;

    private static final int ARGS_SIZE = 3;

    private static final int ZERO_NUMBER = 0;
    private static final int ONE_NUMBER = 1;
    private static final int TWO_NUMBER = 2;
    private static final int THREE_NUMBER = 3;
    private static final int FOUR_NUMBER = 4;
    private static final int FIVE_NUMBER = 5;
    private final ProductPortionRepository productPortionRepository;

    @Override
    public Class handler() {
        return TextHandler.class;
    }

    @Override
    public Object getFindBy() {
        return "TEXT";
    }

    @Override
    @SneakyThrows
    @Transactional
    public Answer getAnswer(final ClassifiedUpdate update, final User user) {
        User newUser = userService.findUserByUpdate(update);

        if (StateType.CREATE_PASSWORD.equals(newUser.getState().getStateType())
                && Objects.equals(newUser.getPassword(), "")) {
            // User is setting the password
            newUser.setPassword(update.getUpdate().getMessage().getText());
            State userNewState = newUser.getState();
            userNewState.setStateType(StateType.ENTER_REFERRAL);
            stateService.save(userNewState);
            userService.save(newUser);

            return new SendMessageBuilder()
                    .chatId(user.getChatId())
                    .message("Password set successfully! If you have referral, please type referral User's hashName:")
                    .build();
        } else if (StateType.ENTER_REFERRAL.equals(newUser.getState().getStateType())) {
            // User is entering referral
            String referralUsername = update.getUpdate().getMessage().getText();
            if (!"no".equalsIgnoreCase(referralUsername)) {
                User referrer = userService.findByChatId(referralUsername);
                if (referrer != null) {
                    List<User> referredUsersReferrals = referrer.getReferrals();
                    referredUsersReferrals.add(newUser);
                    referrer.setReferrals(referredUsersReferrals);
                    userService.save(referrer);
                    newUser.setReferrer(referrer);
                    newUser.getState().setStateType(StateType.NONE);
                    stateService.save(newUser.getState());
                    userService.save(newUser);
                    return new SendMessageBuilder()
                            .chatId(user.getChatId())
                            .message("Referral set successfully! Please select a country:")
                            .buttons(getCountryButtons())
                            .build();
                } else {
                    return new SendMessageBuilder()
                            .chatId(user.getChatId())
                            .message("Referral username not found. Please try again or type 'no' to skip.")
                            .build();
                }

            } else {
                newUser.getState().setStateType(StateType.NONE);
                stateService.save(newUser.getState());
                userService.save(newUser);

                return new SendMessageBuilder()
                        .chatId(user.getChatId())
                        .message("Referral skipped. Please select a country:")
                        .buttons(getCountryButtons())
                        .build();
            }
        } else if (!(UserType.ADMIN.equals(user.getPermissions())
                || UserType.COURIER.equals(user.getPermissions())
                || UserType.MODERATOR.equals(user.getPermissions()))
                && StateType.ORDER.equals(user.getState().getStateType())) {
            String[] parts = user.getState().getValue().split("_");
            Long districtId = Long.parseLong(parts[ZERO_NUMBER]);
            District district = districtService.findById(districtId);
            Long productId = Long.parseLong(parts[ONE_NUMBER]);
            Product product = productService.findById(productId);
            ProductSubcategoryName subcategoryName = ProductSubcategoryName.valueOf(parts[TWO_NUMBER].toUpperCase());
            ProductCategoryName categoryName = ProductCategoryName.valueOf(parts[THREE_NUMBER].toUpperCase());
            Long cityId = Long.parseLong(parts[FOUR_NUMBER]);
            CountryName countryName = CountryName.valueOf(parts[FIVE_NUMBER].toUpperCase());

            int requestedAmount = Integer.parseInt(update.getUpdate().getMessage().getText());
            // Assuming the user's message contains only the amount

            List<ProductPortion> productPortions = productPortionService
                    .findAvailableByDistrictAndProductOrderByCreatedAt(district, product);
            log.info(productPortions.toString());
            int availableAmount = productPortions.stream()
                    .map(ProductPortion::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add).intValue();

            if (availableAmount < requestedAmount) {
                return new SendMessageBuilder()
                        .chatId(user.getChatId())
                        .message("Requested amount is not available. " +
                                "The max available amount is " + availableAmount + ".")
                        .buttons(getMaxAmountButtons(districtId, productId, subcategoryName, categoryName,
                                cityId, countryName, BigDecimal.valueOf(availableAmount)))
                        .build();
            }

            List<ProductPortion> selectedProductPortions = new ArrayList<>();
            for (int i = 0; i < requestedAmount; i++) {
                ProductPortion productPortion = productPortions.get(i);
                selectedProductPortions.add(productPortion);
                productPortionService.reserveProductPortion(productPortion);
            }

            Order order = orderService.createOrder(user, selectedProductPortions);
            selectedProductPortions.forEach(productPortion -> productPortion.setOrder(order));
            productPortionRepository.saveAll(selectedProductPortions);
            basketService.addOrderToBasket(user, order);
            user.getState().setStateType(StateType.NONE);
            user.getState().setValue(null);
            stateService.save(user.getState());
            userService.save(user);

            return new SendMessageBuilder()
                    .chatId(user.getChatId())
                    .message("Order created successfully and added to your basket. " +
                            "You have 30 minutes to buy this order. " +
                            "Product is reserved for you for that time.")
                    .buttons(getBasketOptionsButtons(cityId, countryName))
                    .build();
        } else if (UserType.COURIER.equals(user.getPermissions())
                && (StateType.ADD_PRODUCT_PORTION.equals(user.getState().getStateType())
                || StateType.PRODUCT_PORTION_COUNTRY_CITY_DISTRICT.equals(user.getState().getStateType())
                || StateType.PRODUCT_PORTION_CATEGORY_SUBCATEGORY_PRODUCT.equals(user.getState().getStateType())
                || StateType.PRODUCT_PORTION_LATITUDE_LONGITUDE_AMOUNT.equals(user.getState().getStateType())
                || StateType.PRODUCT_PORTION_PHOTO.equals(user.getState().getStateType()))) {
            return handleProductPortionForm(update, user);
        }
            // Default response
        return new SendMessageBuilder()
                .chatId(user.getChatId())
                .message("Command not recognized.")
                .build();
    }

    private Answer handleProductPortionForm(final ClassifiedUpdate update, final User user) {
        StateType currentState = user.getState().getStateType();

        return switch (currentState) {
            case PRODUCT_PORTION_COUNTRY_CITY_DISTRICT -> handleCountryCityDistrictStep(update, user);
            case PRODUCT_PORTION_CATEGORY_SUBCATEGORY_PRODUCT -> handleCategorySubcategoryProductStep(update, user);
            case PRODUCT_PORTION_LATITUDE_LONGITUDE_AMOUNT -> handleLatitudeLongitudeAmountStep(update, user);
            default -> new SendMessageBuilder()
                    .chatId(user.getChatId())
                    .message("Please provide the required information for the ProductPortion.")
                    .build();
        };
    }

    private Answer handleCountryCityDistrictStep(final ClassifiedUpdate update, final User user) {
        String[] parts = update.getArgs().getFirst().split(" ");
        if (parts.length != ARGS_SIZE) {
            return new SendMessageBuilder()
                    .chatId(user.getChatId())
                    .message("Please provide the country, city, and district separated by spaces.")
                    .build();
        }
        Country country = countryService.findByCountryName(CountryName.valueOf(parts[0].toUpperCase()));
        City city = cityService.findByCountryAndName(country, parts[1]);
        District district = districtService.findByCountryAndCityAndName(country, city, parts[2]);
        if (country == null || city == null || district == null) {
            return new SendMessageBuilder()
                    .chatId(user.getChatId())
                    .message("Invalid country, city, or district. Please try again.")
                    .build();
        }

        // Move to the next step
        user.getState().setStateType(StateType.PRODUCT_PORTION_CATEGORY_SUBCATEGORY_PRODUCT);
        user.getState().setValue(country.getId() + " " + city.getId() + " " + district.getId());
        stateService.save(user.getState());
        userService.save(user);

        return new SendMessageBuilder()
                .chatId(user.getChatId())
                .message("Please provide the product category, subcategory, and product separated by spaces.")
                .build();
    }

    private Answer handleCategorySubcategoryProductStep(final ClassifiedUpdate update,
                                                        final User user) {
        String[] parts = update.getArgs().getFirst().split(" ");
        if (parts.length != ARGS_SIZE) {
            return new SendMessageBuilder()
                    .chatId(user.getChatId())
                    .message("Please provide the product category, subcategory, and product separated by spaces.")
                    .build();
        }
        ProductCategory category = productCategoryService.findByName(parts[0].toUpperCase());
        ProductSubcategory subcategory = productSubcategoryService.findByName(parts[1].toUpperCase());
        Product product = productService.findByName(category, subcategory, parts[2]);
        if (category == null || subcategory == null || product == null) {
            return new SendMessageBuilder()
                    .chatId(user.getChatId())
                    .message("Invalid product category, subcategory, or product. Please try again.")
                    .build();
        }

        // Move to the next step
        user.getState().setStateType(StateType.PRODUCT_PORTION_LATITUDE_LONGITUDE_AMOUNT);
        String countryCityDistrict = user.getState().getValue();
        user.getState().setValue(countryCityDistrict
                + " " + category.getId() + " " + subcategory.getId() + " " + product.getId());
        stateService.save(user.getState());
        userService.save(user);

        return new SendMessageBuilder()
                .chatId(user.getChatId())
                .message("Please provide the latitude, longitude, and amount separated by spaces.")
                .build();
    }

    private Answer handleLatitudeLongitudeAmountStep(final ClassifiedUpdate update, final User user) {
        String[] parts = update.getArgs().getFirst().split(" ");
        if (parts.length != ARGS_SIZE) {
            return new SendMessageBuilder()
                    .chatId(user.getChatId())
                    .message("Please provide the latitude, longitude, and amount separated by spaces.")
                    .build();
        }
        try {
            BigDecimal latitude = new BigDecimal(parts[0]);
            BigDecimal longitude = new BigDecimal(parts[1]);
            BigDecimal amount = new BigDecimal(parts[2]);

            // Move to the next step
            user.getState().setStateType(StateType.PRODUCT_PORTION_PHOTO);
            String countryCityDistrictCategorySubcategoryProduct = user.getState().getValue();
            user.getState().setValue(countryCityDistrictCategorySubcategoryProduct
                    + " " + latitude + " " + longitude + " " + amount);
            stateService.save(user.getState());
            userService.save(user);

            return new SendMessageBuilder()
                    .chatId(user.getChatId())
                    .message("Please upload a photo.")
                    .build();
        } catch (NumberFormatException e) {
            return new SendMessageBuilder()
                    .chatId(user.getChatId())
                    .message("Invalid latitude, longitude, or amount. Please try again.")
                    .build();
        }
    }

    private List<InlineKeyboardButton> getCountryButtons() {
        // Fetch cities from the database with boolean field is_allowed = true
        List<Country> countries = countryService.findAllByAllowedIsTrue();
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        for (Country country : countries) {
            buttons.add(InlineKeyboardButton.builder()
                    .text(String.valueOf(country.getName()))
                    .callbackData("/country_" + country.getName())
                    .build());
        }
        return buttons;
    }

    private List<InlineKeyboardButton> getMaxAmountButtons(final Long districtId, final Long productId,
                                                           final ProductSubcategoryName subcategoryName,
                                                           final ProductCategoryName categoryName, final Long cityId,
                                                           final CountryName countryName,
                                                           final BigDecimal availableAmount) {
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(InlineKeyboardButton.builder()
                .text("Add available amount to basket")
                .callbackData("/district_" + districtId + "_" + productId + "_" + subcategoryName
                        + "_" + categoryName + "_" + cityId + "_" + countryName + "_" + availableAmount)
                .build());

        buttons.add(InlineKeyboardButton.builder()
                .text("Search in other districts")
                .callbackData("/product_" + productId + "_" + subcategoryName + "_" + categoryName
                        + "_" + cityId + "_" + countryName)
                .build());

        return buttons;
    }

    private List<InlineKeyboardButton> getBasketOptionsButtons(final Long cityId, final CountryName countryName) {
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(InlineKeyboardButton.builder()
                .text("Show basket and purchase")
                .callbackData("/basket_")
                .build());

        buttons.add(InlineKeyboardButton.builder()
                .text("Continue shopping")
                .callbackData("/city_" + cityId + "_" + countryName)
                .build());
        return buttons;
    }

}
