package com.telegrambot.marketplace.controller;

import com.telegrambot.marketplace.dto.web.ToggleCityRequestDto;
import com.telegrambot.marketplace.dto.web.ToggleCountryRequestDto;
import com.telegrambot.marketplace.dto.web.ToggleDistrictRequestDto;
import com.telegrambot.marketplace.dto.web.ToggleProductCategoryRequestDto;
import com.telegrambot.marketplace.dto.web.ToggleProductRequestDto;
import com.telegrambot.marketplace.dto.web.ToggleProductSubcategoryRequestDto;
import com.telegrambot.marketplace.dto.web.UnifiedResponseDto;
import com.telegrambot.marketplace.entity.location.City;
import com.telegrambot.marketplace.entity.location.Country;
import com.telegrambot.marketplace.entity.location.District;
import com.telegrambot.marketplace.entity.product.description.Product;
import com.telegrambot.marketplace.entity.product.description.ProductCategory;
import com.telegrambot.marketplace.entity.product.description.ProductSubcategory;
import com.telegrambot.marketplace.entity.user.User;
import com.telegrambot.marketplace.enums.CountryName;
import com.telegrambot.marketplace.enums.ProductCategoryName;
import com.telegrambot.marketplace.enums.ProductSubcategoryName;
import com.telegrambot.marketplace.enums.UserType;
import com.telegrambot.marketplace.exception.NotFoundException;
import com.telegrambot.marketplace.service.entity.CityService;
import com.telegrambot.marketplace.service.entity.CountryService;
import com.telegrambot.marketplace.service.entity.DistrictService;
import com.telegrambot.marketplace.service.entity.ProductCategoryService;
import com.telegrambot.marketplace.service.entity.ProductService;
import com.telegrambot.marketplace.service.entity.ProductSubcategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/toggle")
@Tag(name = "Команды администратора для изменения активности сущностей")
@Slf4j
public class ToggleController {

    private final ProductCategoryService productCategoryService;
    private final ProductSubcategoryService productSubcategoryService;
    private final ProductService productService;
    private final CountryService countryService;
    private final CityService cityService;
    private final DistrictService districtService;

    @Operation(summary = "Изменение активности категории продукта.")
    @ApiResponse(responseCode = "200", description = "ok",
            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
    @ApiResponse(responseCode = "403", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
    @ApiResponse(responseCode = "422", description = "User Not Found",
            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
    @ApiResponse(responseCode = "500", description = "Ошибка сервера",
            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
    @PostMapping("/product_category")
    public UnifiedResponseDto<ProductCategory> toggleProductCategory(
            @RequestBody @Valid final ToggleProductCategoryRequestDto toggleProductCategoryRequestDto) {
        final User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (currentUser == null) {
            throw new NotFoundException("User Not Found");
        }

        if (!UserType.ADMIN.equals(currentUser.getPermissions())) {
            throw new AccessDeniedException("You do not have permission to toggle entities.");
        }

        try {
            ProductCategory category = productCategoryService
                    .findByName(String.valueOf(
                            ProductCategoryName.valueOf(
                                    toggleProductCategoryRequestDto.getProductCategory())));
            if (category == null) {
                throw new NotFoundException("Product Category Not Found");
            }

            category.setAllowed(!category.isAllowed());
            ProductCategory savedProductCategory = productCategoryService.save(category);

            String status = category.isAllowed() ? "available" : "unavailable";

            log.info("Category: {}. New Status: {}", category.getName(), status);

            return new UnifiedResponseDto<>(savedProductCategory);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Product Category has wrong format");
        }
    }

    @Operation(summary = "Изменение активности подкатегории продукта.")
    @ApiResponse(responseCode = "200", description = "ok",
            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
    @ApiResponse(responseCode = "403", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
    @ApiResponse(responseCode = "422", description = "User Not Found",
            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
    @ApiResponse(responseCode = "500", description = "Ошибка сервера",
            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
    @PostMapping("/product_subcategory")
    public UnifiedResponseDto<ProductSubcategory> toggleProductSubcategory(
            @RequestBody @Valid final ToggleProductSubcategoryRequestDto toggleProductSubcategoryRequestDto) {
        final User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (currentUser == null) {
            throw new NotFoundException("User Not Found");
        }

        if (!UserType.ADMIN.equals(currentUser.getPermissions())) {
            throw new AccessDeniedException("You do not have permission to toggle entities.");
        }

        try {
            ProductSubcategory subcategory = productSubcategoryService
                    .findByName(String.valueOf(
                            ProductSubcategoryName.valueOf(
                                    toggleProductSubcategoryRequestDto.getProductSubcategory())));
            if (subcategory == null) {
                throw new NotFoundException("Subcategory Not Found");
            }

            subcategory.setAllowed(!subcategory.isAllowed());
            ProductSubcategory savedProductSubcategory = productSubcategoryService.save(subcategory);

            String status = subcategory.isAllowed() ? "available" : "unavailable";

            log.info("Subcategory: {}. New Status: {}", subcategory.getName(), status);

            return new UnifiedResponseDto<>(savedProductSubcategory);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Product Subcategory has wrong format");
        }
    }

    @Operation(summary = "Изменение активности продукта.")
    @ApiResponse(responseCode = "200", description = "ok",
            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
    @ApiResponse(responseCode = "403", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
    @ApiResponse(responseCode = "422", description = "User Not Found",
            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
    @ApiResponse(responseCode = "500", description = "Ошибка сервера",
            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
    @PostMapping("/product")
    public UnifiedResponseDto<Product> toggleProduct(
            @RequestBody @Valid final ToggleProductRequestDto toggleProductRequestDto) {
        final User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (currentUser == null) {
            throw new NotFoundException("User Not Found");
        }

        if (!UserType.ADMIN.equals(currentUser.getPermissions())) { // Проверяем права доступа пользователя
            throw new AccessDeniedException("You do not have permission to toggle entities.");
        }

        try {
            ProductSubcategory productSubcategory = productSubcategoryService
                    .findByName(String.valueOf(
                            ProductSubcategoryName.valueOf(
                                    toggleProductRequestDto.getProductSubcategory())));
            if (productSubcategory == null) {
                throw new NotFoundException("ProductSubcategory Not Found");
            }

            Product product = productService.findByName(productSubcategory.getProductCategory(),
                    productSubcategory,
                    toggleProductRequestDto.getProductName());
            if (product == null) {
                throw new NotFoundException("Product Not Found");
            }

            product.setAllowed(!product.isAllowed());
            Product savedProduct = productService.save(product);

            String status = product.isAllowed() ? "available" : "unavailable";

            log.info("Product: {}. New Status: {}", product.getName(), status);

            return new UnifiedResponseDto<>(savedProduct);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Product Subcategory has wrong format");
        }
    }

    @Operation(summary = "Изменение активности страны.")
    @ApiResponse(responseCode = "200", description = "ok",
            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
    @ApiResponse(responseCode = "403", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
    @ApiResponse(responseCode = "422", description = "User Not Found",
            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
    @ApiResponse(responseCode = "500", description = "Ошибка сервера",
            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
    @PostMapping("/country")
    public UnifiedResponseDto<Country> toggleCountry(
            @RequestBody @Valid final ToggleCountryRequestDto toggleCountryRequestDto) {
        final User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (currentUser == null) {
            throw new NotFoundException("User Not Found");
        }

        if (!UserType.ADMIN.equals(currentUser.getPermissions())) { // Проверяем права доступа пользователя
            throw new AccessDeniedException("You do not have permission to toggle entities.");
        }

        try {
            Country country = countryService.findByCountryName(
                    CountryName.valueOf(toggleCountryRequestDto.getCountry()));
            if (country == null) {
                throw new NotFoundException("Country Not Found");
            }

            country.setAllowed(!country.isAllowed());
            Country savedCountry = countryService.save(country);

            String status = country.isAllowed() ? "available" : "unavailable";

            log.info("Country: {}. New Status: {}", country.getName(), status);

            return new UnifiedResponseDto<>(savedCountry);
        }  catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Country has wrong format");
        }
    }

    @Operation(summary = "Изменение активности города.")
    @ApiResponse(responseCode = "200", description = "ok",
            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
    @ApiResponse(responseCode = "403", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
    @ApiResponse(responseCode = "422", description = "User Not Found",
            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
    @ApiResponse(responseCode = "500", description = "Ошибка сервера",
            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
    @PostMapping("/city")
    public UnifiedResponseDto<City> toggleCity(
            @RequestBody @Valid final ToggleCityRequestDto toggleCityRequestDto) {
        final User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (currentUser == null) {
            throw new NotFoundException("User Not Found");
        }

        if (!UserType.ADMIN.equals(currentUser.getPermissions())) { // Проверяем права доступа пользователя
            throw new AccessDeniedException("You do not have permission to toggle entities.");
        }

        try {
            Country country = countryService.findByCountryName(CountryName.valueOf(toggleCityRequestDto.getCountry()));
            if (country == null) {
                throw new NotFoundException("Country Not Found");
            }

            City city = cityService.findByCountryAndName(country, toggleCityRequestDto.getCity());
            if (city == null) {
                throw new NotFoundException("City Not Found");
            }

            city.setAllowed(!city.isAllowed());
            City savedCity = cityService.save(city);

            String status = city.isAllowed() ? "available" : "unavailable";

            log.info("City: {}. New Status: {}", city.getName(), status);
            return new UnifiedResponseDto<>(savedCity);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Country has wrong format");
        }
    }

    @Operation(summary = "Изменение активности района.")
    @ApiResponse(responseCode = "200", description = "ok",
            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
    @ApiResponse(responseCode = "403", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
    @ApiResponse(responseCode = "422", description = "User Not Found",
            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
    @ApiResponse(responseCode = "500", description = "Ошибка сервера",
            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
    @PostMapping("/district")
    public UnifiedResponseDto<District> toggleDistrict(
            @RequestBody @Valid final ToggleDistrictRequestDto toggleDistrictRequestDto) {
        final User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (currentUser == null) {
            throw new NotFoundException("User Not Found");
        }

        if (!UserType.ADMIN.equals(currentUser.getPermissions())) {
            throw new AccessDeniedException("You do not have permission to toggle entities.");
        }

        try {
            Country country = countryService.findByCountryName(CountryName.valueOf(
                    toggleDistrictRequestDto.getCountry()));
            if (country == null) {
                throw new NotFoundException("Country Not Found");
            }

            City city = cityService.findByCountryAndName(country, toggleDistrictRequestDto.getCity());
            if (city == null) {
                throw new NotFoundException("City Not Found");
            }

            District district = districtService
                    .findByCountryAndCityAndName(country, city, toggleDistrictRequestDto.getDistrict());
            if (district == null) {
                throw new NotFoundException("District Not Found");
            }

            district.setAllowed(!district.isAllowed());
            District savedDistrict = districtService.save(district);

            String status = district.isAllowed() ? "available" : "unavailable";

            log.info("District: {}. New Status: {}", district, status);
            return new UnifiedResponseDto<>(savedDistrict);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Country has wrong format.");
        }
    }
}
