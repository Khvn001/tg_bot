package com.telegrambot.marketplace.controller;

import com.telegrambot.marketplace.dto.web.UnifiedResponseDto;
import com.telegrambot.marketplace.entity.location.City;
import com.telegrambot.marketplace.entity.location.Country;
import com.telegrambot.marketplace.entity.location.District;
import com.telegrambot.marketplace.entity.product.description.Product;
import com.telegrambot.marketplace.entity.product.description.ProductCategory;
import com.telegrambot.marketplace.entity.product.description.ProductSubcategory;
import com.telegrambot.marketplace.entity.user.User;
import com.telegrambot.marketplace.enums.UserType;
import com.telegrambot.marketplace.exception.NotFoundException;
import com.telegrambot.marketplace.service.entity.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/statistics")
@Tag(name = "Команды администратора для статистики")
public class StatisticsController {
    private final StatisticsService statisticsService;

    @GetMapping("/commands")
    public UnifiedResponseDto<List<String>> getCommandNames() {
        List<String> commandNames = Arrays.asList(
                "product_inventory_city_stats",
                "product_inventory_district_stats",
                "available_countries",
                "unavailable_countries",
                "available_cities",
                "unavailable_cities",
                "available_districts",
                "unavailable_districts",
                "available_product_categories",
                "unavailable_product_categories",
                "available_product_subcategories",
                "unavailable_product_subcategories",
                "available_products",
                "unavailable_products",
                "user_count",
                "sum_user_balances"
        );

        return new UnifiedResponseDto<>(commandNames);
    }

    @Operation(summary = "Получение статистики.")
    @ApiResponse(responseCode = "200", description = "ok",
            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
    @ApiResponse(responseCode = "403", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
    @ApiResponse(responseCode = "422", description = "User Not Found",
            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
    @ApiResponse(responseCode = "500", description = "Ошибка сервера",
            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
    @PostMapping("/statistics")
    public UnifiedResponseDto<String> getStatistics(@RequestParam @Valid final String command) {
        final User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (currentUser == null) {
            throw new NotFoundException("User Not Found");
        }

        if (!UserType.ADMIN.equals(currentUser.getPermissions())) {
            throw new AccessDeniedException("You do not have permission to add products.");
        }

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
        return new UnifiedResponseDto<>(message);
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
