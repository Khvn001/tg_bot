package com.telegrambot.marketplace.service.entity.impl;

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
import com.telegrambot.marketplace.repository.ProductInventoryCityRepository;
import com.telegrambot.marketplace.repository.ProductInventoryDistrictRepository;
import com.telegrambot.marketplace.repository.ProductRepository;
import com.telegrambot.marketplace.repository.ProductSubcategoryRepository;
import com.telegrambot.marketplace.repository.UserRepository;
import com.telegrambot.marketplace.service.entity.StatisticsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final ProductInventoryCityRepository productInventoryCityRepository;
    private final ProductInventoryDistrictRepository productInventoryDistrictRepository;
    private final CountryRepository countryRepository;
    private final CityRepository cityRepository;
    private final DistrictRepository districtRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductSubcategoryRepository productSubcategoryRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public List<Object[]> getProductInventoryCityStats() {
        return productInventoryCityRepository.findGroupedByCityAndProduct();
    }

    @Override
    public List<Object[]> getProductInventoryDistrictStats() {
        return productInventoryDistrictRepository.findGroupedByDistrictAndProduct();
    }

    @Override
    public List<Country> getAvailableCountries() {
        return countryRepository.findByAllowedIsTrue();
    }

    @Override
    public List<Country> getUnavailableCountries() {
        return countryRepository.findByAllowedIsFalse();
    }

    @Override
    public List<City> getAvailableCities() {
        return cityRepository.findByAllowedIsTrue();
    }

    @Override
    public List<City> getUnavailableCities() {
        return cityRepository.findByAllowedIsFalse();
    }

    @Override
    public List<District> getAvailableDistricts() {
        return districtRepository.findByAllowedIsTrue();
    }

    @Override
    public List<District> getUnavailableDistricts() {
        return districtRepository.findByAllowedIsFalse();
    }

    @Override
    public List<ProductCategory> getAvailableProductCategories() {
        return productCategoryRepository.findByAllowedIsTrue();
    }

    @Override
    public List<ProductCategory> getUnavailableProductCategories() {
        return productCategoryRepository.findByAllowedIsFalse();
    }

    @Override
    public List<ProductSubcategory> getAvailableProductSubcategories() {
        return productSubcategoryRepository.findByAllowedIsTrue();
    }

    @Override
    public List<ProductSubcategory> getUnavailableProductSubcategories() {
        return productSubcategoryRepository.findByAllowedIsFalse();
    }

    @Override
    public List<Product> getAvailableProducts() {
        return productRepository.findByAllowedIsTrue();
    }

    @Override
    public List<Product> getUnavailableProducts() {
        return productRepository.findByAllowedIsFalse();
    }

    @Override
    public long getUserCount() {
        return userRepository.count();
    }

    @Override
    public BigDecimal getSumOfUserBalances() {
        return userRepository.sumOfBalances();
    }

}
