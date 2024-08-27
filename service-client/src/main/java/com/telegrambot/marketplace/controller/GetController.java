package com.telegrambot.marketplace.controller;

import com.telegrambot.marketplace.dto.web.UnifiedResponseDto;
import com.telegrambot.marketplace.entity.location.City;
import com.telegrambot.marketplace.entity.location.Country;
import com.telegrambot.marketplace.entity.location.District;
import com.telegrambot.marketplace.entity.product.description.Product;
import com.telegrambot.marketplace.entity.product.description.ProductCategory;
import com.telegrambot.marketplace.entity.product.description.ProductSubcategory;
import com.telegrambot.marketplace.repository.CityRepository;
import com.telegrambot.marketplace.repository.CountryRepository;
import com.telegrambot.marketplace.repository.DistrictRepository;
import com.telegrambot.marketplace.repository.ProductCategoryRepository;
import com.telegrambot.marketplace.repository.ProductRepository;
import com.telegrambot.marketplace.repository.ProductSubcategoryRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/get")
@Tag(name = "Получение списков данных")
public class GetController {

    private final ProductCategoryRepository productCategoryRepository;
    private final ProductSubcategoryRepository productSubcategoryRepository;
    private final ProductRepository productRepository;
    private final CountryRepository countryRepository;
    private final CityRepository cityRepository;
    private final DistrictRepository districtRepository;

    @Operation(summary = "Получить все категории продуктов")
    @ApiResponse(responseCode = "200", description = "Список категорий продуктов")
    @GetMapping("/product-categories")
    public UnifiedResponseDto<List<String>> getProductCategories() {
        final List<String> categories = productCategoryRepository.findAll().stream()
                .map(ProductCategory::getName)
                .map(Objects::toString)
                .collect(Collectors.toList());
        return new UnifiedResponseDto<>(categories);
    }

    @Operation(summary = "Получить все подкатегории продуктов")
    @ApiResponse(responseCode = "200", description = "Список подкатегорий продуктов")
    @GetMapping("/product-subcategories")
    public UnifiedResponseDto<List<String>> getProductSubcategories() {
        final List<String> subcategories = productSubcategoryRepository.findAll().stream()
                .map(ProductSubcategory::getName)
                .map(Objects::toString)
                .collect(Collectors.toList());
        return new UnifiedResponseDto<>(subcategories);
    }

    @Operation(summary = "Получить все продукты")
    @ApiResponse(responseCode = "200", description = "Список продуктов")
    @GetMapping("/products")
    public UnifiedResponseDto<List<String>> getProducts() {
        final List<String> products = productRepository.findAll().stream()
                .map(Product::getName)
                .collect(Collectors.toList());
        return new UnifiedResponseDto<>(products);
    }

    @Operation(summary = "Получить все страны")
    @ApiResponse(responseCode = "200", description = "Список стран")
    @GetMapping("/countries")
    public UnifiedResponseDto<List<String>> getCountries() {
        final List<String> countries = countryRepository.findAll().stream()
                .map(Country::getName)
                .map(Objects::toString)
                .collect(Collectors.toList());
        return new UnifiedResponseDto<>(countries);
    }

    @Operation(summary = "Получить все города")
    @ApiResponse(responseCode = "200", description = "Список городов")
    @GetMapping("/cities")
    public UnifiedResponseDto<List<String>> getCities() {
        final List<String> cities = cityRepository.findAll().stream()
                .map(City::getName)
                .collect(Collectors.toList());
        return new UnifiedResponseDto<>(cities);
    }

    @Operation(summary = "Получить все районы")
    @ApiResponse(responseCode = "200", description = "Список районов")
    @GetMapping("/districts")
    public UnifiedResponseDto<List<String>> getDistricts() {
        final List<String> districts = districtRepository.findAll().stream()
                .map(District::getName)
                .collect(Collectors.toList());
        return new UnifiedResponseDto<>(districts);
    }
}
