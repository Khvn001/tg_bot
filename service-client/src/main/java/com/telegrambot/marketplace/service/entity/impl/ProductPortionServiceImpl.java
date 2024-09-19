package com.telegrambot.marketplace.service.entity.impl;

import com.telegrambot.marketplace.entity.inventory.Photo;
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
import com.telegrambot.marketplace.exception.NotFoundException;
import com.telegrambot.marketplace.repository.ProductInventoryCityRepository;
import com.telegrambot.marketplace.repository.ProductInventoryDistrictRepository;
import com.telegrambot.marketplace.repository.ProductPortionRepository;
import com.telegrambot.marketplace.service.entity.ProductInventoryCityService;
import com.telegrambot.marketplace.service.entity.ProductInventoryDistrictService;
import com.telegrambot.marketplace.service.entity.ProductPortionService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductPortionServiceImpl implements ProductPortionService {

    @PersistenceContext
    private EntityManager entityManager;

    private final ProductPortionRepository productPortionRepository;
    private final ProductInventoryCityService productInventoryCityService;
    private final ProductInventoryDistrictService productInventoryDistrictService;
    private final ProductInventoryCityRepository productInventoryCityRepository;
    private final ProductInventoryDistrictRepository productInventoryDistrictRepository;

    @Override
    public List<ProductPortion> findAvailableProducts(final City city, final Product product) {
        return productPortionRepository.findAllByCityAndProduct(city, product);
    }

    @Override
    public Map<District, List<ProductPortion>> findAvailableDistrictsByMap(final City city, final Product product) {
        List<ProductPortion> availableProducts = findAvailableProducts(city, product);
        log.info(availableProducts.toString());
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
    @Transactional(propagation = Propagation.REQUIRED)
    public void reserveProductPortion(final ProductPortion productPortion) {
        productPortion.setReserved(true);
        productPortionRepository.save(productPortion);
        ProductInventoryCity productInventoryCity = productInventoryCityService
                .findByCityAndProduct(productPortion.getCity(), productPortion.getProduct());
        productInventoryCity.setQuantity(productInventoryCity.getQuantity().subtract(productPortion.getAmount()));
        productInventoryCityService.save(productInventoryCity);

        ProductInventoryDistrict productInventoryDistrict = productInventoryDistrictService
                .findByDistrictAndProduct(productPortion.getDistrict(), productPortion.getProduct());
        productInventoryDistrict.setQuantity(
                productInventoryDistrict.getQuantity().subtract(productPortion.getAmount()));
        productInventoryDistrictService.save(productInventoryDistrict);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void unreserveProductPortion(final ProductPortion productPortion) {
        productPortion.setReserved(false);
        productPortion.setOrder(null);
        ProductPortion savedProductPortion = productPortionRepository.save(productPortion);
        log.info(savedProductPortion.toString());
        ProductInventoryCity productInventoryCity = productInventoryCityService
                .findByCityAndProduct(productPortion.getCity(), productPortion.getProduct());
        productInventoryCity.setQuantity(productInventoryCity.getQuantity().add(productPortion.getAmount()));
        ProductInventoryCity savedProductInventoryCity = productInventoryCityService.save(productInventoryCity);
        log.info(savedProductInventoryCity.toString());

        ProductInventoryDistrict productInventoryDistrict = productInventoryDistrictService
                .findByDistrictAndProduct(productPortion.getDistrict(), productPortion.getProduct());
        productInventoryDistrict.setQuantity(productInventoryDistrict.getQuantity().add(productPortion.getAmount()));
        ProductInventoryDistrict savedProductInventoryDistrict =
                productInventoryDistrictService.save(productInventoryDistrict);
        log.info(savedProductInventoryDistrict.toString());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ProductPortion saveProductPortion(final User user, final Country country, final City city,
                                             final District district, final ProductCategory category,
                                             final ProductSubcategory subcategory, final Product product,
                                             final BigDecimal latitude, final BigDecimal longitude,
                                             final BigDecimal amount, final List<String> photoUrls) {

        // Ensure user is managed by merging
        User managedCourier = entityManager.merge(user);

        // Create new ProductPortion
        ProductPortion productPortion = new ProductPortion();
        productPortion.setCourier(managedCourier);
        productPortion.setCountry(country);
        productPortion.setCity(city);
        productPortion.setDistrict(district);
        productPortion.setProductCategory(category);
        productPortion.setProductSubcategory(subcategory);
        productPortion.setProduct(product);
        productPortion.setLatitude(latitude);
        productPortion.setLongitude(longitude);
        productPortion.setAmount(amount);
        productPortion.setCreatedAt(LocalDateTime.now());

        // Save the ProductPortion
        ProductPortion savedProductPortion = productPortionRepository.save(productPortion);

        // Create Photo entities for each photoUrl and associate with the ProductPortion
        List<Photo> photos = new ArrayList<>();
        for (String photoUrl : photoUrls) {
            Photo photo = new Photo();
            photo.setProductPortion(savedProductPortion);
            photo.setPhotoUrl(photoUrl);
            photos.add(photo);
        }
        savedProductPortion.setPhotos(photos);

        // Save the updated ProductPortion with its photos
        savedProductPortion = productPortionRepository.save(savedProductPortion);
        log.info(savedProductPortion.toString());

        // Increase the quantity in ProductInventoryDistrict
        ProductInventoryDistrict productInventoryDistrict = productInventoryDistrictRepository
                .findByDistrictAndProduct(district, product)
                .orElse(
                        new ProductInventoryDistrict(
                                null, product, subcategory, category, district, city, country, BigDecimal.ZERO));
        productInventoryDistrict.setQuantity(productInventoryDistrict.getQuantity().add(amount));
        ProductInventoryDistrict savedProductInventoryDistrict =
                productInventoryDistrictRepository.save(productInventoryDistrict);
        log.info(savedProductInventoryDistrict.toString());

        // Increase the quantity in ProductInventoryCity
        ProductInventoryCity productInventoryCity = productInventoryCityRepository
                .findByCityAndProduct(city, product)
                .orElse(
                        new ProductInventoryCity(
                                null, product, subcategory, category, city, country, BigDecimal.ZERO));
        productInventoryCity.setQuantity(productInventoryCity.getQuantity().add(amount));
        ProductInventoryCity savedProductInventoryCity =
                productInventoryCityRepository.save(productInventoryCity);
        log.info(savedProductInventoryCity.toString());
        return savedProductPortion;
    }

    @Override
    public List<ProductPortion> getProductPortionsByCourier(final User courier) {
        return productPortionRepository.findByCourier(courier);
    }

    @Override
    public List<ProductPortion> getProductPortionsByCity(final City city) {
        return productPortionRepository.findByCity(city);
    }

    @Override
    public List<ProductPortion> getProductPortionsByDistrict(final District district) {
        return productPortionRepository.findByDistrict(district);
    }

    @Override
    public List<ProductPortion> getProductPortionsBySubcategory(final ProductSubcategory subcategory) {
        return productPortionRepository.findByProductSubcategory(subcategory);
    }

    @Override
    public List<ProductPortion> getProductPortionsByProduct(final Product product) {
        return productPortionRepository.findByProduct(product);
    }

    @Override
    @Transactional
    public void deleteProductPortion(final Long productPortionId) {
        if (productPortionRepository.existsById(productPortionId)) {
            productPortionRepository.deleteById(productPortionId);
        } else {
            throw new NotFoundException("Product Portion not found with ID: " + productPortionId);
        }
    }

    @Override
    public ProductPortion findById(final Long productPortionId) {
        return productPortionRepository.findById(productPortionId)
                .orElseThrow(() -> new NotFoundException("Product Portion not found with ID: " + productPortionId));
    }
}
