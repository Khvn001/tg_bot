package com.telegrambot.marketplace.command.admin;

import com.telegrambot.marketplace.dto.bot.Answer;
import com.telegrambot.marketplace.dto.bot.ClassifiedUpdate;
import com.telegrambot.marketplace.entity.location.City;
import com.telegrambot.marketplace.entity.location.Country;
import com.telegrambot.marketplace.entity.location.District;
import com.telegrambot.marketplace.entity.product.description.Product;
import com.telegrambot.marketplace.entity.product.description.ProductCategory;
import com.telegrambot.marketplace.entity.product.description.ProductSubcategory;
import com.telegrambot.marketplace.entity.user.User;
import com.telegrambot.marketplace.enums.UserType;
import com.telegrambot.marketplace.dto.bot.SendMessageBuilder;
import com.telegrambot.marketplace.service.entity.StatisticsService;
import com.telegrambot.marketplace.config.typehandlers.CommandHandler;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class StatisticsCommand implements AdminCommand {

    private final StatisticsService statisticsService;

    @Override
    public Class handler() {
        return CommandHandler.class;
    }

    @Override
    public Object getFindBy() {
        return "/admin_statistics_";
    }

    @Override
    @SneakyThrows
    public Answer getAnswer(final ClassifiedUpdate update, final User user) {
        if (!UserType.ADMIN.equals(user.getPermissions())) {
            return new SendMessageBuilder()
                    .chatId(user.getChatId())
                    .message("You do not have permission to get statistics.")
                    .build();
        }
        String command = update.getCommandName();
        String message = switch (command) {
            case "product_inventory_city_stats" -> generateProductInventoryCityStatsMessage();
            case "product_inventory_district_stats" -> generateProductInventoryDistrictStatsMessage();
            case "available_countries" ->
                    generateCountryListMessage(statisticsService.getAvailableCountries(),
                            "Available Countries");
            case "unavailable_countries" ->
                    generateCountryListMessage(statisticsService.getUnavailableCountries(),
                            "Unavailable Countries");
            case "available_cities" ->
                    generateCityListMessage(statisticsService.getAvailableCities(),
                            "Available Cities");
            case "unavailable_cities" ->
                    generateCityListMessage(statisticsService.getUnavailableCities(),
                            "Unavailable Cities");
            case "available_districts" ->
                    generateDistrictListMessage(statisticsService.getAvailableDistricts(),
                            "Available Districts");
            case "unavailable_districts" ->
                    generateDistrictListMessage(statisticsService.getUnavailableDistricts(),
                            "Unavailable Districts");
            case "available_product_categories" ->
                    generateProductCategoryListMessage(statisticsService.getAvailableProductCategories(),
                            "Available Product Categories");
            case "unavailable_product_categories" ->
                    generateProductCategoryListMessage(statisticsService.getUnavailableProductCategories(),
                            "Unavailable Product Categories");
            case "available_product_subcategories" ->
                    generateProductSubcategoryListMessage(statisticsService.getAvailableProductSubcategories(),
                            "Available Product Subcategories");
            case "unavailable_product_subcategories" ->
                    generateProductSubcategoryListMessage(statisticsService.getUnavailableProductSubcategories(),
                            "Unavailable Product Subcategories");
            case "available_products" ->
                    generateProductListMessage(statisticsService.getAvailableProducts(),
                            "Available Products");
            case "unavailable_products" ->
                    generateProductListMessage(statisticsService.getUnavailableProducts(),
                            "Unavailable Products");
            case "user_count" -> "Number of all users: " + statisticsService.getUserCount();
            case "sum_user_balances" -> "Sum of all user balances: " + statisticsService.getSumOfUserBalances();
            default -> "Unknown statistics command.";
        };
        return new SendMessageBuilder()
                .chatId(user.getChatId())
                .message(message)
                .build();
    }

    private String generateProductInventoryCityStatsMessage() {
        List<Object[]> stats = statisticsService.getProductInventoryCityStats();
        StringBuilder message = new StringBuilder("Product Inventory City Statistics:\n");
        for (Object[] stat : stats) {
            message.append("City: ").append(stat[0]).append(", Product: ").append(stat[1])
                    .append(", Quantity: ").append(stat[2]).append("\n");
        }
        return message.toString();
    }

    private String generateProductInventoryDistrictStatsMessage() {
        List<Object[]> stats = statisticsService.getProductInventoryDistrictStats();
        StringBuilder message = new StringBuilder("Product Inventory District Statistics:\n");
        for (Object[] stat : stats) {
            message.append("District: ").append(stat[0]).append(", Product: ").append(stat[1])
                    .append(", Quantity: ").append(stat[2]).append("\n");
        }
        return message.toString();
    }

    private String generateCountryListMessage(final List<Country> countries, final String title) {
        StringBuilder message = new StringBuilder(title + ":\n");
        for (Country country : countries) {
            message.append(country.getName()).append("\n");
        }
        return message.toString();
    }

    private String generateCityListMessage(final List<City> cities, final String title) {
        StringBuilder message = new StringBuilder(title + ":\n");
        for (City city : cities) {
            message.append(city.getName()).append("\n");
        }
        return message.toString();
    }

    private String generateDistrictListMessage(final List<District> districts, final String title) {
        StringBuilder message = new StringBuilder(title + ":\n");
        for (District district : districts) {
            message.append(district.getName()).append("\n");
        }
        return message.toString();
    }

    private String generateProductCategoryListMessage(final List<ProductCategory> productCategories,
                                                      final String title) {
        StringBuilder message = new StringBuilder(title + ":\n");
        for (ProductCategory category : productCategories) {
            message.append(category.getName()).append("\n");
        }
        return message.toString();
    }

    private String generateProductSubcategoryListMessage(final List<ProductSubcategory> productSubcategories,
                                                         final String title) {
        StringBuilder message = new StringBuilder(title + ":\n");
        for (ProductSubcategory subcategory : productSubcategories) {
            message.append(subcategory.getName()).append("\n");
        }
        return message.toString();
    }

    private String generateProductListMessage(final List<Product> products, final String title) {
        StringBuilder message = new StringBuilder(title + ":\n");
        for (Product product : products) {
            message.append(product.getName()).append("\n");
        }
        return message.toString();
    }
}
