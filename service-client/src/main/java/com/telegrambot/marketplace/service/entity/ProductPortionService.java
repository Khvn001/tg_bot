package com.telegrambot.marketplace.service.entity;

import com.telegrambot.marketplace.entity.inventory.ProductPortion;
import com.telegrambot.marketplace.entity.location.City;
import com.telegrambot.marketplace.entity.location.Country;
import com.telegrambot.marketplace.entity.location.District;
import com.telegrambot.marketplace.entity.product.description.Product;
import com.telegrambot.marketplace.entity.product.description.ProductCategory;
import com.telegrambot.marketplace.entity.product.description.ProductSubcategory;
import com.telegrambot.marketplace.entity.user.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ProductPortionService {

    List<ProductPortion> findAvailableProducts(City city, Product product);

    Map<District, List<ProductPortion>> findAvailableDistrictsByMap(City city, Product product);

    List<ProductPortion> findAvailableByDistrictAndProductOrderByCreatedAt(District district, Product product);

    void reserveProductPortion(ProductPortion productPortion);

    void unreserveProductPortion(ProductPortion productPortion);

    void saveCountryCityDistrict(User user, Country country, City city, District district);

    void saveCategorySubcategoryProduct(User user,
                                        ProductCategory category,
                                        ProductSubcategory subcategory,
                                        Product product);

    void saveLatitudeLongitudeAmount(User user, BigDecimal latitude, BigDecimal longitude, BigDecimal amount);

    void savePhoto(User user, String photoUrl);
}
