package com.telegrambot.marketplace.config.typehandlers;

import com.telegrambot.marketplace.command.courier.PhotoCommand;
import com.telegrambot.marketplace.command.TextCommand;
import com.telegrambot.marketplace.command.admin.StatisticsCommand;
import com.telegrambot.marketplace.command.admin.add.AddCityCommand;
import com.telegrambot.marketplace.command.admin.add.AddCountryCommand;
import com.telegrambot.marketplace.command.admin.add.AddDistrictCommand;
import com.telegrambot.marketplace.command.admin.add.AddProductCategoryCommand;
import com.telegrambot.marketplace.command.admin.add.AddProductCommand;
import com.telegrambot.marketplace.command.admin.add.AddProductSubcategoryCommand;
import com.telegrambot.marketplace.command.admin.availability.ToggleCityAvailabilityCommand;
import com.telegrambot.marketplace.command.admin.availability.ToggleCountryAvailabilityCommand;
import com.telegrambot.marketplace.command.admin.availability.ToggleDistrictAvailabilityCommand;
import com.telegrambot.marketplace.command.admin.availability.ToggleProductAvailabilityCommand;
import com.telegrambot.marketplace.command.admin.availability.ToggleProductCategoryAvailabilityCommand;
import com.telegrambot.marketplace.command.admin.availability.ToggleProductSubcategoryAvailabilityCommand;
import com.telegrambot.marketplace.command.user.basket.BasketCommand;
import com.telegrambot.marketplace.command.user.basket.BuyBasketCommand;
import com.telegrambot.marketplace.command.user.basket.DeleteOrderCommand;
import com.telegrambot.marketplace.command.user.basket.DeleteOrdersCommand;
import com.telegrambot.marketplace.command.user.basket.OrdersCommand;
import com.telegrambot.marketplace.command.user.pathway.CategoryCommand;
import com.telegrambot.marketplace.command.user.pathway.CityCommand;
import com.telegrambot.marketplace.command.user.pathway.CountryCommand;
import com.telegrambot.marketplace.command.user.pathway.DistrictCommand;
import com.telegrambot.marketplace.command.user.pathway.ProductCommand;
import com.telegrambot.marketplace.command.user.pathway.StartCommand;
import com.telegrambot.marketplace.command.user.pathway.SubcategoryCommand;
import com.telegrambot.marketplace.command.user.profile.AddBalanceCommand;
import com.telegrambot.marketplace.command.user.profile.ProfileInfoViewCommand;
import com.telegrambot.marketplace.service.entity.CountryService;
import com.telegrambot.marketplace.service.entity.StateService;
import com.telegrambot.marketplace.service.entity.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class CommandConfig {

    @Bean
    public CommandHandler commandHandler(
            final AddCountryCommand addCountryCommand,
            final AddCityCommand addCityCommand,
            final AddDistrictCommand addDistrictCommand,
            final AddProductCategoryCommand addProductCategoryAdminCommand,
            final AddProductSubcategoryCommand addProductSubcategoryAdminCommand,
            final AddProductCommand addProductCommand,
            final ToggleCountryAvailabilityCommand toggleCountryAvailabilityCommand,
            final ToggleCityAvailabilityCommand toggleCityAvailabilityCommand,
            final ToggleDistrictAvailabilityCommand toggleDistrictAvailabilityCommand,
            final ToggleProductCategoryAvailabilityCommand toggleProductCategoryAvailabilityCommand,
            final ToggleProductSubcategoryAvailabilityCommand toggleProductSubcategoryAvailabilityCommand,
            final ToggleProductAvailabilityCommand toggleProductAvailabilityCommand,
            final StatisticsCommand statisticsCommand,
            final StartCommand startCommand) {

        CommandHandler commandHandler = new CommandHandler();

        // Register user commands
        commandHandler.registerUserCommand("/start", startCommand);

        // Register admin commands
        commandHandler.registerAdminCommand("/admin_add_product_category_", addProductCategoryAdminCommand);
        commandHandler.registerAdminCommand("/admin_add_product_subcategory_", addProductSubcategoryAdminCommand);
        commandHandler.registerAdminCommand("/admin_add_product_", addProductCommand);
        commandHandler.registerAdminCommand("/admin_add_country_", addCountryCommand);
        commandHandler.registerAdminCommand("/admin_add_city_", addCityCommand);
        commandHandler.registerAdminCommand("/admin_add_district_", addDistrictCommand);
        commandHandler.registerAdminCommand("/admin_toggle_country_availability_", toggleCountryAvailabilityCommand);
        commandHandler.registerAdminCommand("/admin_toggle_city_availability_", toggleCityAvailabilityCommand);
        commandHandler.registerAdminCommand("/admin_toggle_district_availability_", toggleDistrictAvailabilityCommand);
        commandHandler.registerAdminCommand("/admin_toggle_category_availability_",
                toggleProductCategoryAvailabilityCommand);
        commandHandler.registerAdminCommand("/admin_toggle_subcategory_availability_",
                toggleProductSubcategoryAvailabilityCommand);
        commandHandler.registerAdminCommand("/admin_toggle_product_availability_", toggleProductAvailabilityCommand);
        commandHandler.registerAdminCommand("/admin_statistics_", statisticsCommand);

        // Add other commands as needed
        log.info(commandHandler.toString());

        return commandHandler;
    }

    @Bean
    public TextHandler textHandler(final TextCommand textCommand) {
        TextHandler textHandler = new TextHandler();
        textHandler.registerTextCommand("TEXT", textCommand);
        log.info(textHandler.toString());

        return textHandler;
    }

    @Bean
    public CallbackHandler callbackHandler(final BasketCommand basketCommand,
                                           final BuyBasketCommand buyBasketCommand,
                                           final DeleteOrderCommand deleteOrderCommand,
                                           final DeleteOrdersCommand deleteOrdersCommand,
                                           final OrdersCommand ordersCommand,
                                           final CountryCommand countryCommand,
                                           final CityCommand cityCommand,
                                           final DistrictCommand districtCommand,
                                           final CategoryCommand categoryCommand,
                                           final SubcategoryCommand subcategoryCommand,
                                           final ProductCommand productCommand,
                                           final ProfileInfoViewCommand profileInfoViewCommand,
                                           final AddBalanceCommand addBalanceCommand,
                                           final UserService userService,
                                           final StateService stateService,
                                           final CountryService countryService) {
        CallbackHandler callbackHandler = new CallbackHandler(userService, stateService, countryService);
        callbackHandler.registerCallbackCommand("/basket_", basketCommand);
        callbackHandler.registerCallbackCommand("/buyBasket_", buyBasketCommand);
        callbackHandler.registerCallbackCommand("/deleteOrder_", deleteOrderCommand);
        callbackHandler.registerCallbackCommand("/deleteOrders_", deleteOrdersCommand);
        callbackHandler.registerCallbackCommand("/orders_", ordersCommand);
        callbackHandler.registerCallbackCommand("/category_", categoryCommand);
        callbackHandler.registerCallbackCommand("/subcategory_", subcategoryCommand);
        callbackHandler.registerCallbackCommand("/product_", productCommand);
        callbackHandler.registerCallbackCommand("/country_", countryCommand);
        callbackHandler.registerCallbackCommand("/city_", cityCommand);
        callbackHandler.registerCallbackCommand("/district_", districtCommand);
        callbackHandler.registerCallbackCommand("/profile_", profileInfoViewCommand);
        callbackHandler.registerCallbackCommand("/add_balance_", addBalanceCommand);
        log.info(callbackHandler.toString());
        return callbackHandler;
    }

    @Bean
    public PhotoHandler photoHandler(final PhotoCommand photoCommand) {
        PhotoHandler photoHandler = new PhotoHandler();
        photoHandler.registerPhotoCommand("PHOTO", photoCommand);
        log.info(photoHandler.toString());
        return photoHandler;
    }
}
