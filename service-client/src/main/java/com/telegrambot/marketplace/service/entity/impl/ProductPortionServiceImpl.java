package com.telegrambot.marketplace.service.entity.impl;

import com.telegrambot.marketplace.entity.inventory.ProductInventoryCity;
import com.telegrambot.marketplace.entity.inventory.ProductInventoryDistrict;
import com.telegrambot.marketplace.entity.inventory.ProductPortion;
import com.telegrambot.marketplace.entity.location.City;
import com.telegrambot.marketplace.entity.location.Country;
import com.telegrambot.marketplace.entity.location.District;
import com.telegrambot.marketplace.entity.product.description.Product;
import com.telegrambot.marketplace.entity.product.description.ProductCategory;
import com.telegrambot.marketplace.entity.product.description.ProductSubcategory;
import com.telegrambot.marketplace.entity.user.User;
import com.telegrambot.marketplace.repository.ProductPortionRepository;
import com.telegrambot.marketplace.service.entity.ProductInventoryCityService;
import com.telegrambot.marketplace.service.entity.ProductInventoryDistrictService;
import com.telegrambot.marketplace.service.entity.ProductPortionService;
import com.telegrambot.marketplace.service.entity.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductPortionServiceImpl implements ProductPortionService {

    private final ProductPortionRepository productPortionRepository;
    private final ProductInventoryCityService productInventoryCityService;
    private final ProductInventoryDistrictService productInventoryDistrictService;
    private final UserService userService;

    @Override
    public List<ProductPortion> findAvailableProducts(final City city, final Product product) {
        return productPortionRepository.findAllByCityAndProduct(city, product);
    }

    @Override
    public Map<District, List<ProductPortion>> findAvailableDistrictsByMap(final City city, final Product product) {
        List<ProductPortion> availableProducts = findAvailableProducts(city, product);
        Map<District, List<ProductPortion>> categoryMap = availableProducts.stream()
                .filter(productPortion -> productPortion.getDistrict().isAllowed())
                .collect(Collectors.groupingBy(ProductPortion::getDistrict));
        categoryMap.entrySet().removeIf(entry -> entry.getValue().isEmpty());

        return categoryMap;
    }

    public List<ProductPortion> findAllByDistrictAndProduct(final District district, final Product product) {
        return productPortionRepository.findAllByDistrictAndProduct(district, product);
    }

    public void updateAmount(final ProductPortion productPortion, final BigDecimal amount) {
        productPortion.setAmount(productPortion.getAmount().add(amount));
        productPortionRepository.save(productPortion);
    }

    @Override
    public List<ProductPortion> findAvailableByDistrictAndProductOrderByCreatedAt(final District district,
                                                                                  final Product product) {
        return productPortionRepository.findByDistrictAndProductAndReservedFalseOrderByCreatedAtAsc(district, product);
    }

    @Override
    public void reserveProductPortion(final ProductPortion productPortion) {
        productPortion.setReserved(true);
        productPortionRepository.save(productPortion);
        ProductInventoryCity productInventoryCity = productInventoryCityService
                .findByCityAndProduct(productPortion.getCity(), productPortion.getProduct());
        productInventoryCity.setQuantity(productInventoryCity.getQuantity().subtract(BigDecimal.valueOf(1)));
        productInventoryCityService.save(productInventoryCity);

        ProductInventoryDistrict productInventoryDistrict = productInventoryDistrictService
                .findByDistrictAndProduct(productPortion.getDistrict(), productPortion.getProduct());
        productInventoryDistrict.setQuantity(productInventoryCity.getQuantity().subtract(BigDecimal.valueOf(1)));
        productInventoryDistrictService.save(productInventoryDistrict);
    }

    @Override
    public void unreserveProductPortion(final ProductPortion productPortion) {
        productPortion.setReserved(false);
        ProductInventoryCity productInventoryCity = productInventoryCityService
                .findByCityAndProduct(productPortion.getCity(), productPortion.getProduct());
        productInventoryCity.setQuantity(productInventoryCity.getQuantity().add(BigDecimal.valueOf(1)));
        productInventoryCityService.save(productInventoryCity);

        ProductInventoryDistrict productInventoryDistrict = productInventoryDistrictService
                .findByDistrictAndProduct(productPortion.getDistrict(), productPortion.getProduct());
        productInventoryDistrict.setQuantity(productInventoryCity.getQuantity().add(BigDecimal.valueOf(1)));
        productPortionRepository.save(productPortion);
    }

    @Override
    public void saveCountryCityDistrict(final User user,
                                        final Country country,
                                        final City city,
                                        final District district) {
        ProductPortion productPortion = new ProductPortion();
        productPortion.setCountry(country);
        productPortion.setCity(city);
        productPortion.setDistrict(district);
        // Save the productPortion associated with the user in a temporary storage
        user.setCourierTemporaryProductPortion(productPortion);
    }

    @Override
    public void saveCategorySubcategoryProduct(final User user,
                                               final ProductCategory category,
                                               final ProductSubcategory subcategory,
                                               final Product product) {
        ProductPortion productPortion = user.getCourierTemporaryProductPortion();
        productPortion.setProductCategory(category);
        productPortion.setProductSubcategory(subcategory);
        productPortion.setProduct(product);
    }

    @Override
    public void saveLatitudeLongitudeAmount(final User user,
                                            final BigDecimal latitude,
                                            final BigDecimal longitude,
                                            final BigDecimal amount) {
        ProductPortion productPortion = user.getCourierTemporaryProductPortion();
        productPortion.setLatitude(latitude);
        productPortion.setLongitude(longitude);
        productPortion.setAmount(amount);
    }

    @Override
    public void savePhoto(final User user, final String photoUrl) {
        ProductPortion productPortion = user.getCourierTemporaryProductPortion();
        productPortion.setPhotoUrl(photoUrl);
        productPortion.setCreatedAt(LocalDateTime.now());
        productPortion.setReserved(false);
        // Persist the productPortion in the repository
        productPortionRepository.save(productPortion);

        ProductInventoryCity productInventoryCity = productInventoryCityService
                .findByCityAndProduct(productPortion.getCity(), productPortion.getProduct());
        productInventoryCity.setQuantity(productInventoryCity.getQuantity().add(BigDecimal.valueOf(1)));
        productInventoryCityService.save(productInventoryCity);

        ProductInventoryDistrict productInventoryDistrict = productInventoryDistrictService
                .findByDistrictAndProduct(productPortion.getDistrict(), productPortion.getProduct());
        productInventoryDistrict.setQuantity(productInventoryCity.getQuantity().add(BigDecimal.valueOf(1)));
        productInventoryDistrictService.save(productInventoryDistrict);

        user.setCourierTemporaryProductPortion(null); // Clear the temporary storage
        userService.save(user);
    }

}