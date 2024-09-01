package com.telegrambot.marketplace.controller;

import com.telegrambot.marketplace.dto.web.AddCityRequestDto;
import com.telegrambot.marketplace.dto.web.AddCountryRequestDto;
import com.telegrambot.marketplace.dto.web.AddDistrictRequestDto;
import com.telegrambot.marketplace.dto.web.AddProductCategoryRequestDto;
import com.telegrambot.marketplace.dto.web.AddProductRequestDto;
import com.telegrambot.marketplace.dto.web.AddProductSubcategoryRequestDto;
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
import com.telegrambot.marketplace.exception.DatabaseEntryAlreadyExistsException;
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
@RequestMapping("/api/v1/add")
@Tag(name = "Команды администратора для добавления сущностей")
@Slf4j
public class AddController {

    private final ProductCategoryService productCategoryService;
    private final ProductSubcategoryService productSubcategoryService;
    private final ProductService productService;
    private final CountryService countryService;
    private final CityService cityService;
    private final DistrictService districtService;

    @Operation(summary = "Добавление новой категории продукта.")
    @ApiResponse(responseCode = "200", description = "ok",
            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
    @ApiResponse(responseCode = "403", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
    @ApiResponse(responseCode = "422", description = "User Not Found",
            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
    @ApiResponse(responseCode = "500", description = "Ошибка сервера",
            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
    @PostMapping("/product_category")
    public UnifiedResponseDto<ProductCategory> addProductCategory(
            @RequestBody @Valid final AddProductCategoryRequestDto addProductCategoryRequestDto) {
        final User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (currentUser == null) {
            throw new NotFoundException("User Not Found");
        }

        if (!UserType.ADMIN.equals(currentUser.getPermissions())) {
            throw new AccessDeniedException("You do not have permission to add entities.");
        }

        try {
            ProductCategory category = productCategoryService
                    .findByName(
                            String.valueOf(
                                    ProductCategoryName.valueOf(addProductCategoryRequestDto.getProductCategory())));

            if (category != null) {
                throw new DatabaseEntryAlreadyExistsException("Product Category already exists");
            }

            category = new ProductCategory();
            category.setName(ProductCategoryName.valueOf(addProductCategoryRequestDto.getProductCategory()));
            category.setAllowed(true);
            ProductCategory savedProductCategory = productCategoryService.save(category);
            log.info("Added product category '{}'", savedProductCategory);
            return new UnifiedResponseDto<>(savedProductCategory);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid product category name: "
                    + addProductCategoryRequestDto.getProductCategory());
        }
    }

    @Operation(summary = "Добавление новой подкатегории продукта.")
    @ApiResponse(responseCode = "200", description = "ok",
            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
    @ApiResponse(responseCode = "403", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
    @ApiResponse(responseCode = "422", description = "User Not Found",
            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
    @ApiResponse(responseCode = "500", description = "Ошибка сервера",
            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
    @PostMapping("/product_subcategory")
    public UnifiedResponseDto<ProductSubcategory> addProductSubcategory(
            @RequestBody @Valid final AddProductSubcategoryRequestDto addProductSubcategoryRequestDto) {
        final User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (currentUser == null) {
            throw new NotFoundException("User Not Found");
        }

        if (!UserType.ADMIN.equals(currentUser.getPermissions())) {
            throw new AccessDeniedException("You do not have permission to add entities.");
        }

        try {
            ProductCategory category = productCategoryService
                    .findByName(
                            String.valueOf(
                                    ProductCategoryName.valueOf(addProductSubcategoryRequestDto.getProductCategory())));
            if (category == null) {
                throw new NotFoundException("Product Category Not Found");
            }

            ProductSubcategory subcategory = productSubcategoryService
                    .findByName(
                            String.valueOf(
                                    ProductSubcategoryName.valueOf(
                                            addProductSubcategoryRequestDto.getProductSubcategory())));
            if (subcategory != null) {
                throw new DatabaseEntryAlreadyExistsException("Subcategory already exists.");
            }

            subcategory = new ProductSubcategory();
            subcategory.setProductCategory(category);
            subcategory.setName(ProductSubcategoryName.valueOf(
                    addProductSubcategoryRequestDto.getProductSubcategory()));
            subcategory.setAllowed(true);
            ProductSubcategory savedProductSubcategory = productSubcategoryService.save(subcategory);
            log.info("Added productSubcategory '{}' to '{}'", savedProductSubcategory, category.getName());
            return new UnifiedResponseDto<>(savedProductSubcategory);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Product Category or Subcategory names are incorrect");
        }
    }

    @Operation(summary = "Добавление нового продукта.")
    @ApiResponse(responseCode = "200", description = "ok",
            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
    @ApiResponse(responseCode = "403", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
    @ApiResponse(responseCode = "422", description = "User Not Found",
            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
    @ApiResponse(responseCode = "500", description = "Ошибка сервера",
            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
    @PostMapping("/product")
    public UnifiedResponseDto<Product> addProduct(
            @RequestBody @Valid final AddProductRequestDto addProductRequestDto) {
        final User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (currentUser == null) {
            throw new NotFoundException("User Not Found");
        }

        if (!UserType.ADMIN.equals(currentUser.getPermissions())) {
            throw new AccessDeniedException("You do not have permission to add entities.");
        }

        Product product = new Product();
        product.setName(addProductRequestDto.getProductName());
        product.setAllowed(true);

        try {
            ProductSubcategory subcategory = productSubcategoryService
                    .findByName(addProductRequestDto.getProductSubcategory());
            if (subcategory == null) {
                throw new NotFoundException("Product Subcategory Not Found");
            }

            product.setProductSubcategory(subcategory);
            product.setProductCategory(subcategory.getProductCategory());

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid Product Subcategory name");
        }

        product.setDescription(addProductRequestDto.getDescription());
        product.setPhotoUrl("");
        product.setPrice(addProductRequestDto.getPrice());
        Product savedProduct = productService.save(product);

        log.info("Added product '{}' to '{}'", savedProduct.getName(), savedProduct.getProductSubcategory().getName());

        return new UnifiedResponseDto<>(savedProduct);
    }

    @Operation(summary = "Добавление новой страны.")
    @ApiResponse(responseCode = "200", description = "ok",
            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
    @ApiResponse(responseCode = "403", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
    @ApiResponse(responseCode = "422", description = "User Not Found",
            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
    @ApiResponse(responseCode = "500", description = "Ошибка сервера",
            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
    @PostMapping("/country")
    public UnifiedResponseDto<Country> addCountry(
            @RequestBody @Valid final AddCountryRequestDto addCountryRequestDto) {
        final User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (currentUser == null) {
            throw new NotFoundException("User Not Found");
        }

        if (!UserType.ADMIN.equals(currentUser.getPermissions())) {
            throw new AccessDeniedException("You do not have permission to add entities.");
        }

        try {
            Country country = countryService.findByCountryName(CountryName.valueOf(addCountryRequestDto.getCountry()));
            if (country != null) {
                throw new DatabaseEntryAlreadyExistsException("Country Already Exists");
            }

            country = new Country();
            country.setName(CountryName.valueOf(addCountryRequestDto.getCountry()));
            country.setAllowed(true);
            Country savedCountry = countryService.save(country);

            log.info("Added country: {}", country);

            return new UnifiedResponseDto<>(savedCountry);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid country name: " + addCountryRequestDto.getCountry());
        }
    }

    @Operation(summary = "Добавление нового города.")
    @ApiResponse(responseCode = "200", description = "ok",
            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
    @ApiResponse(responseCode = "403", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
    @ApiResponse(responseCode = "422", description = "User Not Found",
            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
    @ApiResponse(responseCode = "500", description = "Ошибка сервера",
            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
    @PostMapping("/city")
    public UnifiedResponseDto<City> addCity(
            @RequestBody @Valid final AddCityRequestDto addCityRequestDto) {
        final User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (currentUser == null) {
            throw new NotFoundException("User Not Found");
        }

        if (!UserType.ADMIN.equals(currentUser.getPermissions())) {
            throw new AccessDeniedException("You do not have permission to add entities.");
        }

        try {
            Country country = countryService.findByCountryName(CountryName.valueOf(addCityRequestDto.getCountry()));
            if (country == null) {
                throw new NotFoundException("Country Not Found");
            }

            City city = cityService.findByCountryAndName(country, addCityRequestDto.getCity());

            if (city != null) {
                throw new DatabaseEntryAlreadyExistsException("City Already Exists");
            }

            city = new City();
            city.setName(addCityRequestDto.getCity());
            city.setCountry(country);
            city.setAllowed(true);
            City savedCity = cityService.save(city);

            log.info("Added city '{}' to country '{}'", addCityRequestDto.getCity(), addCityRequestDto.getCountry());

            return new UnifiedResponseDto<>(savedCity);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Country has bad format");
        }
    }

    @Operation(summary = "Добавление нового района.")
    @ApiResponse(responseCode = "200", description = "ok",
            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
    @ApiResponse(responseCode = "403", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
    @ApiResponse(responseCode = "422", description = "User Not Found",
            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
    @ApiResponse(responseCode = "500", description = "Ошибка сервера",
            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
    @PostMapping("/district")
    public UnifiedResponseDto<District> addDistrict(
            @RequestBody @Valid final AddDistrictRequestDto addDistrictRequestDto) {
        final User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (currentUser == null) {
            throw new NotFoundException("User Not Found");
        }

        if (!UserType.ADMIN.equals(currentUser.getPermissions())) {
            throw new AccessDeniedException("You do not have permission to add entities.");
        }

        try {
            Country country = countryService
                    .findByCountryName(CountryName.valueOf(addDistrictRequestDto.getCountry()));
            if (country == null) {
                throw new NotFoundException("Country Not Found");
            }
            City city = cityService.findByCountryAndName(country, addDistrictRequestDto.getCity());
            if (city == null) {
                throw new NotFoundException("City Not Found");
            }

            District district = districtService
                    .findByCountryAndCityAndName(country, city, addDistrictRequestDto.getDistrict());
            if (district != null) {
                throw new DatabaseEntryAlreadyExistsException("District Already Exists");
            }
            district = new District();
            district.setName(addDistrictRequestDto.getDistrict());
            district.setCountry(country);
            district.setCity(city);
            district.setAllowed(true);
            District savedDistrict = districtService.save(district);

            log.info("Added district {} to city {} in country {}",
                    district.getName(), city.getName(), country.getName());

            return new UnifiedResponseDto<>(savedDistrict);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Country is bad format");
        }
    }

}
